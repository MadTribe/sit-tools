package com.paul.gtd;
import com.paul.gtd.tools.*;
import static com.paul.gtd.tools.Display.*;
import com.paul.gtd.tools.jira.*;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class HSBCAnalysis {

		public static void main(String[] args){
			if (args.length > 0){
				def inputFileName = args[0];
				def outFile = inputFileName + ".json";
				def issues = new JiraExportReader().readExport(inputFileName)
				def reader = new DataReader();
				def tools = new SituationTools();
				def data = tools.init(outFile);

				def readItem = { listItem ->
						listItem["Key"] = listItem["Key"].replace("-","")
						def issue = new GraphItem(listItem["Key"], listItem["Summary"], "GOAL",false, true);
						issue.properties = listItem;
						return issue;
				}

				def readRelationships = { listItem ->
						def currentId = listItem["Key"];
						def readRelationships = [];
						listItem["Linked Issues"].each { link ->
								def linkArr = [currentId, "NEEDS", link.replace("-","")];
								readRelationships << linkArr;
						}
						return readRelationships;
				}

				def coreIssues = issues.findAll{ issue ->
						issue["Labels"].contains("Core-MVP");
				}

				def isGoal = { issue ->
						issue.properties["Labels"]?.contains("page") == true;
				}


				def dataModel = reader.buildDataModelGenericTreeDataSource(coreIssues, outFile, readItem, readRelationships, isGoal);

  			reader.save(dataModel );

				def graphVizExporter = new GraphVizExporter();
				graphVizExporter.nodeColor = { node ->
						def color = "red";
						if (node.properties["Labels"]?.contains("page") == true){
								color = "blue";
						}
						println node.properties["Status"]
						if (node.properties["Status"] == "Done."){
								color = "green";
						}
						return color;
				}

				graphVizExporter.generateUndirectedGraphFromDot(dataModel);

				// issues.each { issue ->
				// 		prints "> " + issue["Key"] + "  " + issue["Issue Type"] + "  " + issue["Status"] + "  " + issue["Linked Issues"]
				// };
			} else {
				prints "jimport <fileName>"
			}
		}


}
