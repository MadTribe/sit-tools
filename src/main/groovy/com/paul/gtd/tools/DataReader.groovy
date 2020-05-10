package com.paul.gtd.tools;
import org.yaml.snakeyaml.Yaml;
import java.text.SimpleDateFormat;
import groovy.json.JsonBuilder;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import com.paul.gtd.model.*;
import static com.paul.gtd.tools.Display.*;

public class DataReader {

  def load(String filePath) {
      return new JsonSlurper().parseText(new File(filePath).text)
  }

  def save(GraphModel model) {
    println model
    println model.fileName
    def file = new File(model.fileName);
    def path = file.getParentFile().getPath();
    String timestamp = new Date().format( 'yyyy-MM-dd_hh_mm_ss');

    def backup = path + "/" + timestamp + "_" + file.name ;

    try {
      new File(backup).bytes = new File(model.fileName).bytes
    } catch (FileNotFoundException nfe){
      println "Could not backup original file."
    }

  //	println JsonOutput.prettyPrint(JsonOutput.toJson(model));

     new File(model.fileName).write(model.toJson());
  }

  def buildDataModel(data, fileName){
    def dataModel = new GraphModel();
    dataModel.fileName = fileName;

    if (data["settings"] != null){
        dataModel.settings = data["settings"];

    }

    data.items.forEach{ item ->
      //def model = new GraphItem(item.id, item.fullName, item.type, item.complete);
      def model = new GraphItem(item);
      if (item.properties){
        model.properties = item.properties;
      }

      if (item.timeNeeded){
        model.timeNeeded = item.timeNeeded;
      }

      dataModel.items[item.id] = model;
      if (model.type == ItemTypes.FINAL_GOAL){
        dataModel.finalGoal = model;
      }
      return dataModel;
    }

    if (dataModel.finalGoal == null){
      throw new RuntimeException("No node has been set with type 'final_goal'");
    }

    data.relationships.forEach{ rel ->
      def fromItemId = rel[0];
      def relKey = rel[1];
      def toItemId = rel[2];

      def fromModel = dataModel.items[fromItemId];
      def toModel = dataModel.items[toItemId];

      if (!fromModel){
        if (dataModel.strict){
          throw new RuntimeException("Failure loading relation because Item with id " + fromItemId + " does not exist.");
        } else {
          System.out.print("Warning Item with id " + fromItemId + " does not exist.");
          dataModel.missingNodes << suggestNode(fromItemId);
          fromModel = new GraphItem(fromItemId, fromItemId);
        }
      }

      if (!toModel){

        if (dataModel.strict){
          throw new RuntimeException("Failure loading relation because Item with id " + toItemId + " does not exist.");
        } else {
          System.out.print("Warning Item with id " + toItemId + " does not exist.");
          dataModel.missingNodes << suggestNode(toItemId);
          toModel = new GraphItem(toItemId, toItemId);
        }

      }

      def relModel = new Rel(fromModel,toModel);
      relModel.type = RelationsTypes.valueOf(relKey);

      dataModel.relationships << relModel;
      fromModel.outboundRelationships << relModel
      toModel.inboundRelationships << relModel

    }

     return dataModel;
  }

  /**
   * @param items - list of items
   * @param fileName
   * @param readItem - output a GraphItem from a list item
   * @param readRelationships - output a relationship array from a list item ["id1", "NEEDS", "id2"]
   */
  def buildDataModelGenericTreeDataSource(items,
                                         fileName,
                                         readItem,
                                         readRelationships,
                                         isGoal = { item -> false}){
    def dataModel = new GraphModel();
    dataModel.fileName = fileName;

    items.forEach{ item ->

      def model = readItem(item);
      println model.id;

      dataModel.items[model.id] = model;
      if (model.type == ItemTypes.FINAL_GOAL){
        dataModel.finalGoal = model;
      }

    }

    def rootNode = new GraphItem("root", "complete", "FINAL_GOAL", false);
    dataModel.items[rootNode.id] = rootNode;
    items.forEach{ item ->
      def relationships = readRelationships(item);
      relationships.forEach{ rel ->
        def fromItemId = rel[0];
        def relKey = rel[1];
        def toItemId = rel[2];

        def fromModel = dataModel.items[fromItemId];
        def toModel = dataModel.items[toItemId];

        if (!fromModel || !toModel){
          if (!fromModel){
              println "Error ${fromItemId} not found"
          }

          if (!toModel){
              println "Error ${toItemId} not found"
          }
        } else {

          if (isGoal(fromModel)){
            def relModel = fromModel.addOutboundRelationship(toModel, RelationsTypes.valueOf(relKey))

            dataModel.relationships << relModel;
          }

        }
      }

    }

    dataModel.items.each { k,v ->
       if ( isGoal(v)){
           def rel = rootNode.addOutboundRelationship(v)
           dataModel.relationships << rel;
       }

    }
    return dataModel;
  }


}
