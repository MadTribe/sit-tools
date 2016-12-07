package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class TreeParser {

  def parseTree(node, parseId, context, operation, depth = 0, row =0){
    println depth;
    def currentRow = row;
    if (!parseId){
      Random random = new Random();
      parseId = "" + random.nextInt(10 ** 3);
    }

    if (node.visitId != parseId){
      node.visitId = parseId;

      operation(node, depth, currentRow, context);

      node.outboundRelationships.each{ child ->
        currentRow++;
        currentRow = parseTree(child.to, parseId, context, operation, depth + 1, currentRow);

      }



    }
    return currentRow;

  }

}
