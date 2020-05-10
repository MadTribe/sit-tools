package com.paul.gtd.tools

import com.paul.gtd.model.ItemTypes

import java.awt.*
import java.nio.file.Files
import java.nio.file.Paths

import static com.paul.gtd.tools.Time.hoursToDuration;

public class GraphVizExporter {


	def fixText(xid){
		return xid.replaceAll("\"","");
	}


	def createDotNode(node, dataModel){
		def color = "#2597bf";

		if (node.type == ItemTypes.FINAL_GOAL){
			color = "yellow";
		} else if (node.complete){
			color = "green";
		} else {
			color = doNodeColor(node);
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

	String doNodeColor(node) {
		Long now = new Date().time;
		Long lastUpdate = node.lastUpdate;


		if (lastUpdate == null){
			return "green";
		} else {
			float daysSinceLastEdit = (now - lastUpdate) / (1000 * 60 * 60 * 24);
println daysSinceLastEdit;
			def col = getScaledColor([255,255,255], [249, 0,0],daysSinceLastEdit/5)
			println col;
			return encodeColor(col);
		}

	}

	public static String encodeColor(colArray) {
		Color color = new Color((int)colArray[0] , (int)colArray[1] , (int)colArray[2]);
		return "#" + String.format("%06x", color.getRGB() & 0xffffff);

	}

	def getScaledColor(startCol, endCol,  fraction){
        println "$startCol, $endCol, $fraction"
		def ret = [0,0,0];

		if (fraction > 1){
			fraction = 1;
		}

		if (fraction < 0){
			fraction = 0;
		}

		ret[0] = scaleNum(startCol[0], endCol[0], fraction);
		ret[1] = scaleNum(startCol[1], endCol[1], fraction);
		ret[2] = scaleNum(startCol[2], endCol[2], fraction);

		return ret;
	}

	def float scaleNum(int start, int end,  fraction){
		if (fraction > 1){
			fraction = 1;
		}

		if (fraction < 0){
			fraction = 0;
		}

		return ((end - start) * fraction) + start;

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
