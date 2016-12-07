package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class GraphVizExporter {
	def nodeColor = null;

	def fixText(xid){
		return xid.replaceAll("\"","");
	}


	def createDotNode(node, dataModel){
		def color = "#2597bf";

		if (nodeColor == null){
			if (node.type == ItemTypes.FINAL_GOAL){
				color = "yellow";
			}
			if (node.complete){
				color = "green";
			}
		} else {
			color = nodeColor(node);

		}

		def label = "${fixText(node.fullName)}";

		if (dataModel.settings["reportEstimates"]){

			label += "  ${hoursToDuration(node.timeNeeded)}";
		}

		""" ${node.id} [shape=box, color=blue, style=filled, fillcolor="${color}",  label= "${label}" ];\n """
	}

	def createDotEdge(rel){
		""" ${rel.from.id} -> ${rel.to.id} [style=solid, label="${rel.type.toString().toLowerCase()}" ] ; \n"""
	}

	def saveAsDot(dataModel, fileName){



		def sb = "digraph graphname {\n";

		dataModel.items.forEach { k,v ->
			sb += createDotNode(v, dataModel);
		}

		dataModel.relationships.forEach { v ->
			sb += createDotEdge(v);
		}


		sb += "}\n";

		def outFile = new File(fileName);

		outFile << sb;

	}

	def createFilename(fileName){
			def file = new File(fileName);
			def path = file.getParentFile().getCanonicalPath();
			def name = file.name;
	    String timestamp = new Date().format( 'yyyy-MM-dd_hh_mm_ss');

			return path + "/" + timestamp + name + "out.dot";
	}


	def generateUndirectedGraphFromDot(dataModel){
		def outfile = createFilename(dataModel.fileName);
		saveAsDot(dataModel, outfile)
		println outfile;
		("fdp " + outfile + " -Tsvg  -O").execute();

		displayImage(outfile);
	}

	def generateDigraphFromDot(dataModel){
		def outfile = createFilename(dataModel.fileName);
		saveAsDot(dataModel, outfile)
		println outfile;
		("dot " + outfile + " -Tsvg  -O").execute();

		displayImage(outfile);
	}

	def displayImage(outfile){
		def file = new File(outfile + ".svg");
		def path = file.toURI();

		 // "/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome".execute();

		 "/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome chinapws/2016-10-29_12_23_40chinapws.jsonout.dot.svg".execute();

	}


}
