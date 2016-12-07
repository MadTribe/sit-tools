package com.paul.gtd.tools;
import org.yaml.snakeyaml.Yaml;
import java.text.SimpleDateFormat;
import groovy.json.JsonBuilder;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import com.paul.gtd.model.*;
import static com.paul.gtd.tools.Display.*;

/**
 * This is a situational modellign tool that can be used in project planning
 */

public class SituationTools {


	def suggestNode(idName){
		return  """
			{
				"id":"${idName}",
				"fullName" : "${idName}",
				"type" : "goal"
			},
		""";
	}

	def parseTree(node, parseId, context, operation, depth = 0){

		if (!parseId){
			Random random = new Random();
			parseId = "" + random.nextInt(10 ** 3);
		}

		if (node.visitId != parseId){
			node.visitId = parseId;

			node.outboundRelationships.each{ child ->

				parseTree(child.to, parseId, context, operation, depth + 1);

			}

			operation(node, depth);
		}


	}


	def printMissingNodes(dataModel){
		dataModel.missingNodes.forEach { v ->
			println v;
		}

	}

	class UserExitedException extends Exception{

	}

	def improveEstimates(dataModel){

		println "improveEstimates"

		def leafNodesOperation = { node , depth->
			if (node.outboundRelationships.size() == 0){
				if (node.timeNeeded == 0){

					def hours = System.console().readLine 'How many hours do you need for? ' + node.fullName;
					if (!"X".equals(hours)){
						try {
							node.timeNeeded = Integer.valueOf(hours);
						} catch (Exception e){
							println "Error " + e;
						}
					} else {
						throw new UserExitedException();
					}

				}
			}

		};

		def fillInIntermediateEstimates = { node, depth ->

			if (node.outboundRelationships.size() > 0){
				int totalChildTime = 0;

				node.outboundRelationships.each { child ->
					totalChildTime += child.to.timeNeeded;
				}
				prints "total child time " + totalChildTime;

				if (node.timeNeeded < totalChildTime){
					node.timeNeeded = totalChildTime;
				}

			}

		};




		try {
			parseTree(dataModel.finalGoal, null, [:], leafNodesOperation);
			parseTree(dataModel.finalGoal, null, [:], fillInIntermediateEstimates);
		} catch (UserExitedException uee){
			println "later dude";
		}
	}

  def calcTasksWorth(dataModel){

	  println "Evaluating task worth V2"

    dataModel.items.each { k, node ->

        node.inboundRelationships.each { inboundLink ->
            node.worth += inboundLink.from.inboundRelationships.size() + inboundLink.from.relativeWorth;
        }
        node.worth *= node.relativeWorth;
    }

	}

	def calcTasksWorthv1(dataModel){

	  println "Evaluating task worth"

	  def valueTasks = { node , depth->


			def totalRelativeWorth = 0;
	    def opens = node.outboundRelationships.find{ item ->

					totalRelativeWorth += item.to.relativeWorth;
					!item.to.complete;
			}

	    if ( node.relativeWorth){
				def nodeWorth = node.worth > 0 ? node.worth : node.relativeWorth;
	      opens.each { child ->
	          child.to.worth = nodeWorth * child.to.relativeWorth / totalRelativeWorth;

	      }
	    } else {
				node.worth = 0;
			}

	  };

	  try {
	    parseTree(dataModel.finalGoal, null, [:], valueTasks);

	  } catch (UserExitedException uee){
	    println "later dude ";
	  }
	}

	def addNodeTo(item, name, dataModel, relType = RelationsTypes.NEEDS, childType = ItemTypes.GOAL){

		def id = item.id + "." + (item.outboundRelationships.size() + 1);
		def child = new GraphItem(id, name, childType.name(), false);

		def rel = item.addOutboundRelationship(child, relType);

		dataModel.items[id] = child;
		dataModel.relationships << rel;

		return child;

	}

	def linkTask(item, dataModel){
		def term = getInput("Search For node to link");
		def searcher = new GraphSearcher([model: dataModel ]);

		def results = searcher.search(term);

		def idx = 0;
		results.each{ res ->
				prints("${idx++}  ${res.fullName}  ${res.path()}");
		}
		def choice = getNumber("select item to link");

		if (choice != null){
				def toLink = results[choice];

				println "Will link ${toLink.fullName}"
				def rel = item.addOutboundRelationship(toLink, RelationsTypes.NEEDS);
				dataModel.relationships << rel;
		} else {
				def res = getInput("Would you like to create a new child (y/n)").toLowerCase();
				if ( res == "y"){
					expandTask(item, dataModel)
				}
		}

	}


	def expandTask(item, dataModel){
			println "Enter F to finish.";
			def name = System.console().readLine 'New SubTask Name: ';
			if (name == "F" || name == "f"){
				return false;
			}

			def actionable = System.console().readLine 'Is actionable (y/n): ';

			def newChild = addNodeTo(item, name, dataModel);

			if (actionable == "y"){
				newChild.actionable = true;
			}

			return true;
	}

	def completeTask(item, dataModel){
			item.complete = true;

			def result = getInput("What was the reslt?");
			def child = addNodeTo(item, result, dataModel, RelationsTypes.RESULT);


	}

  def doSummaryReport(node){

          def summary = new SummaryReporter();
          summary.createReportData(node);

  }

	def workOnItem(item, dataModel){
    def space = "    ";
		println """How about ${item.path()}?
${space}Options:
${space}(S)kip = I don't know or I'm not interested right now."""
    if (item.actionable != true){
		  println "${space}(A)ctionable = Mark Actionable"
    } else {
      println "${space}(N)ot Actionable = Mark Not Actionable"
    }
println """
${space}(D)one = Finished Already
${space}(E)xpand = I need to add some children to this.
${space}(L)ink = Link to other nodes
${space}(W)worth (relative) set relative worth of this.
${space}(R)eport print a summary Report.
${space}(Q)uit = quit this item.
		"""

		def option = System.console().readLine '? ';

		option = option.toLowerCase();

		if (option == "q"){
				 return false;
		}

		if (option != "s"){
			 if (option == "d"){
				 		completeTask(item, dataModel);
			 }

			 if (option == "a"){
				 		item.actionable = true;
			 }

       if (option == "n"){
				 		item.actionable = false;
			 }

			 if (option == "w"){
				 		item.relativeWorth = getNumber("Enter Relative Worth");
			 }

			 if (option == "l"){
			 	   linkTask(item, dataModel);
			 }


			 if (option == "e"){
						while (expandTask(item, dataModel)){

						}
			 }

       if (option == "r"){
            doSummaryReport(item);
       }
		} else {
			 println "Will look at this later.\n\n";
		}

		return true;

	}

	def planMode(dataModel){
		prints "Plan Mode";
		calcTasksWorth(dataModel);

		def annotateDepth = { item , depth ->
			item.depth = depth;
		}

		parseTree(dataModel.finalGoal, null, [:], annotateDepth);

		def contin = true;

		dataModel.items.clone().values().sort{ item -> 0 - item.worth}.forEach{ item ->

			if (contin){

				if (item.outboundRelationships.size() == 0){

					if (!item.complete){

						if (!workOnItem(item, dataModel)){
								contin = false;
						}
					}

				}
			}

		};

	}

	def narrativePlan(dataModel){
		prints "Everything Mode";
		calcTasksWorth(dataModel);

		def annotateDepth = { item , depth ->
			item.depth = depth;
		}


		def contin = true;

		dataModel.items.clone().values().sort{ item -> 0 - item.worth}.forEach{ item ->

				if (item.outboundRelationships.size() > 0 ){
					prints "GOAL: "
				}
				prints item.fullName + "\n";

		};

	}


	def workMode(dataModel){
		prints "Work Mode";

		def contin = true;

    calcTasksWorth(dataModel);

		dataModel.items.clone().values().sort{ item -> 0 - item.worth}.forEach{  item ->

			if (contin){

				if (item.actionable){

					if (!item.complete){

						if (!workOnItem(item, dataModel)){
								contin = false;
						}
					}

				}
			}

		};

	}

	def toDoList(dataModel){



		 while (true){
			 def todoItems = dataModel.items.clone().values().findAll{ item -> item.actionable == true && !item.complete && item.outboundRelationships.size() == 0  }.sort{ item -> 0 - item.worth}

				 for (int i = 0; i < todoItems.size(); i++){
		  	 		 def child = todoItems[i];
						 prints "    ${i}.  ${child.fullName}     ${child.path()}";
				 }
				 def sel = getInput("Select index or ! for exit browse.");


				 if (sel == "!"){
					 return null;
				 } else {

					 int selectedIdx = 0;
					 try {
						 selectedIdx =  Integer.valueOf(sel);
						 def selectedNode = todoItems[selectedIdx];
						 workOnItem(selectedNode, dataModel);
					 } catch (Exception e){
						 e.printStackTrace();
						 prints "Invalid Selection ${sel}"
					 }

				 }
	   }


	}

	def interactive( dataModel){
			prints "interactive mode";
      def cont = true;

      while (cont){
  			prints "b) browse to find item";
  			prints "p) planning mode";
  			prints "w) work mode";
				prints "t) todo list mode";
				prints "n) narrative"
				prints "image) net";
				prints "dig) digraph";
				prints "q) quit"


  			def option = System.console().readLine '? ';
  			if (option.toLowerCase() == "b"){
  					def browser = new TreeBrowser();
  					def selectedItem = browser.browse(dataModel.finalGoal);
  					while (selectedItem) {

  						if (selectedItem){
  							workOnItem(selectedItem, dataModel);
  						}
  						def parent = selectedItem.inboundRelationships[0]?.from;
  						if (parent){
  							selectedItem = browser.browse(parent);
  						} else {
								selectedItem = null;
							}
  					}

  			}

  			if (option.toLowerCase() == "w"){
  					workMode(dataModel);
  			}

  			if (option.toLowerCase() == "p"){
  					planMode(dataModel);
  			}

				if (option.toLowerCase() == "t"){
  					toDoList(dataModel);
  			}

				if (option.toLowerCase() == "n"){
					narrativePlan(dataModel);
				}

				if (option.toLowerCase() == "image"){
					def graphVizExporter = new GraphVizExporter();
  				graphVizExporter.generateDigraphFromDot(dataModel);
  			}

        if (option.toLowerCase() == "dig"){
					def graphVizExporter = new GraphVizExporter();
  				graphVizExporter.generateDigraphFromDot(dataModel);
  			}

        if (option.toLowerCase() == "q"){
            cont = false;
        }
      }
	}


	def init(fileName){
			println "Initializing Project ${fileName}"
			def root = "Complete"

			def template = """{
			"relationships" : [],
				 "items" : [
			    {
				    "id":"${root}",
				    "fullName" : "${root}",
				    "type" : "FINAL_GOAL",
						"complete" : false
			    }   ]
			}
""";
			def data = new JsonSlurper().parseText(template)
			println data;
			return data;

	}


	def parseArgs(String[] args){
		args.each { opt ->
			Context.commandOptions.add(opt);
		}
	}

  public static void main(String[] args){
  	try {
			def tools = new SituationTools();
			def reader = new DataReader();


  		tools.parseArgs(args);

  		println(args.length);
  		if (args.length > 0){

  			def finalArgIdx = args.length - 1;
  			def inputFileName = args[finalArgIdx];

				def data = null;
				if (!Context.commandOptions.contains("init")){
  				 data = reader.load(inputFileName);
				} else {
					data = tools.init(inputFileName);
				}
  			def dataModel = reader.buildDataModel(data, inputFileName);
				tools.calcTasksWorth(dataModel)

  			tools.printMissingNodes(dataModel);

				if (Context.commandOptions.contains("int")){
  				Context.commandOptions.add("save")
  				tools.interactive(dataModel);
  			}

  			if (Context.commandOptions.contains("work")){
  				Context.commandOptions.add("save")
  				tools.workMode(dataModel);
  			}

				if (Context.commandOptions.contains("plan")){
  				Context.commandOptions.add("save")
  				tools.planMode(dataModel);
  			}

				if (Context.commandOptions.contains("todo")) {
						Context.commandOptions.add("save")
						tools.toDoList(dataModel)
				}

  			if (Context.commandOptions.contains("size")){
  				Context.commandOptions.add("save")
  				tools.improveEstimates(dataModel);
  			}

  			if (Context.commandOptions.contains("save")){
  				reader.save(dataModel );
  			}

  			if (Context.commandOptions.contains("image")){
					def graphVizExporter = new GraphVizExporter();
  				graphVizExporter.generateDigraphFromDot(dataModel);
  			}

        if (Context.commandOptions.contains("dig")){
					def graphVizExporter = new GraphVizExporter();
  				graphVizExporter.generateDigraphFromDot(dataModel);
  			}

        if (Context.commandOptions.contains("fdp")){
					def graphVizExporter = new GraphVizExporter();


  				graphVizExporter.generateUndirectedGraphFromDot(dataModel);
  			}



  		} else {
  			tools.printUsage();
  		}
  	} catch (Throwable e){
  		e.printStackTrace();
  	}
  }

	def printUsage(){
	def ret = """
		Usage:
			groovy situationTools [OPTIONS] <situation_file.json>

			OPTIONS:
					save - saves updated data model to <situation_file.json>2
					size - asks you about estimates for each leaf task
					dig  - digraph image
					fdp  - non directed graph image
					work - work mode
					plan - plan mode
					int  - interactuve mode
					init - create empty file
					todo - prints todo list
	""";
		println ret;
	}

}
