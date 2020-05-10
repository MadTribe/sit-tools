package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class GanttExporter {

	def nodeColor = null;

	def fixText(xid){
		return xid.replaceAll("\"","");
	}



   def saveAsTJ(dataModel){
		def outfile = createFilename(dataModel.fileName)

		StringBuffer sb = new StringBuffer();
		sb.append( "task ${dataModel.finalGoal?.fixId(dataModel.finalGoal.fullName)} \"${dataModel.finalGoal.fullName}\" {\n");
		taskNNode(dataModel.finalGoal, "    ", sb);
		sb.append("}\n");


		println "will output " + sb
		println "to " + outfile;

		def outFile = new File(outfile);
		outFile << sb;
	}


	def createFilename(fileName){
		def file = new File(fileName);
		def path = file.getParentFile().getCanonicalPath();
		def name = file.name;
		String timestamp = new Date().format( 'yyyy-MM-dd_hh_mm_ss');

		def ret = path + "/" + timestamp + name + "out.tji";
		return ret;
	}



    def taskNNode(rootNode, indent, sb){
        

        if (rootNode.outboundRelationships.size() == 0){
          return rootNode;
        }

        for (int i = 0; i < rootNode.outboundRelationships.size(); i++){
            def child = rootNode.outboundRelationships[i].to;

			if (child.inboundRelationships[0].type != RelationsTypes.RESULT){

				sb.append( indent + "task ${child?.fixId(child.fullName)} \"${child.fullName}\" {\n");

				if (child.outboundRelationships.size() == 0 ){
					sb.append( indent + "    duration 1d\n");

				}

				taskNNode(child, indent + '   ', sb);
				sb.append(indent + "}\n");
			}
			
        }
    

    }

	def generateUndirectedGraphFromDot(dataModel){
		def outfile = createFilename(dataModel.fileName);
		saveAsDot(dataModel, outfile)
		println outfile;
		("fdp " + outfile + " -Tsvg  -O").execute();

		displayImage(outfile);
	}




}
