package com.paul.gtd.tools;
import static com.paul.gtd.tools.Display.*;

public class TreeBrowser {

    def browse(rootNode){
        prints "Current Node is ${rootNode.fullName}";

        if (rootNode.outboundRelationships.size() == 0){
          return rootNode;
        }

        for (int i = 0; i < rootNode.outboundRelationships.size(); i++){
            def child = rootNode.outboundRelationships[i].to;
            def complete = "Open";
            if (child.complete ) complete = "Complete";
            printf("%-4d) %-40s %-8s worth=%-4d children=%-4d \n", i, child.fullName, complete, child.relativeWorth, child.outboundRelationships.size() );
        }
        def sel = getInput("Select child index or '.' for current or '..' for parent or ! for exit browse.");

        if (sel == "."){
          return rootNode;
        } else if (sel == ".."){
          def selectedNode = rootNode.inboundRelationships[0].from;
          return browse(selectedNode)
        } else if (sel == "!"){
          return null;
        } else {

          int selectedIdx = 0;
          try {
            selectedIdx =  Integer.valueOf(sel);
            def selectedNode = rootNode.outboundRelationships[selectedIdx].to;
            return browse(selectedNode)
          } catch (Exception e){
            prints "Invalid Selection, choosing current node."
            return rootNode;
          }

        }

    }

}
