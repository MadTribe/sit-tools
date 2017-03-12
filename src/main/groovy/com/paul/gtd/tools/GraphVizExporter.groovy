package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;
import java.nio.file.*;

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

		def hrefProp = "";
		if (node.url){
				hrefProp = """, href= "${node.url}" """;
		}

		""" ${node.id} [shape=box, color=blue, style=filled, fillcolor="${color}",  label= "${label}" ${hrefProp}];\n """
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

	def bashisePath(path){
		def ret = path.replaceAll(" ","\\\\ ");
		return ret;
	}

	def getParentFolder(fileName){
		def file = new File(fileName);
		def path = file.getParentFile().getCanonicalPath();
		return path;
	}

	def createFilename(fileName){
		def file = new File(fileName);
		def path = file.getParentFile().getCanonicalPath();
			def name = file.name;
	    String timestamp = new Date().format( 'yyyy-MM-dd_hh_mm_ss');

			def ret = path + "/" + timestamp + name + "out.dot";
			return ret;
	}


	def generateUndirectedGraphFromDot(dataModel){
		def outfile = createFilename(dataModel.fileName);
		saveAsDot(dataModel, outfile)

		def cmd = ["fdp", outfile, "-Tsvg", "-O"];
		println cmd;
		(cmd).execute().consumeProcessOutput(System.out, System.err);

		displayImage(outfile);
	}

	def generateDigraphFromDot(dataModel){
		def outfile = createFilename(dataModel.fileName);
		saveAsDot(dataModel, outfile)
		def cmd = ["dot", outfile, "-Tsvg", "-O"];
		println cmd;
		(cmd).execute().consumeProcessOutput(System.out, System.err);

		displayImage(outfile);
	}

	def displayImage(outfile){
		def file = new File(outfile + ".svg");
		def path = file.toURI();


		 ['/Applications/Google Chrome.app/Contents/MacOS/Google Chrome', path].execute().consumeProcessOutput(System.out, System.err)


		 def to = Paths.get(outfile)
		 def from = Paths.get(getParentFolder(outfile))

		 def latest = from.resolve("latest.svg");

		 try {
		     Files.delete(latest);
		 } catch (Exception x) {
		     System.err.format("%s: no such" + " file or directory%n", path);
		 }


		 try {
			 Files.createSymbolicLink( from.resolve("latest.svg"), file.toPath());
		 } catch (Exception x) {
		 		System.err.format("%s: no such" + " file or directory%n", path);
		 }




	}


}
