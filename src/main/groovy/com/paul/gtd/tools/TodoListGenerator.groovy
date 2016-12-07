package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class TodoListGenerator {
	private static final String COLS = "cols";

	def createReportData(reportRoot){
			def treeParser = new TreeParser();

			def buildCols = {node, colNum, rowNum, context ->

					 def columns = context[COLS];
					 if (  columns[colNum] == null){
					 		columns[colNum] = [];
					 }

					 columns[colNum][rowNum] = node.fullName;

			}

			def context = [:]
			def columns = [];
			context[COLS] = columns;

			treeParser.parseTree(reportRoot, null, context, buildCols)

			int numRows = 1;
			for (int row=0; row < numRows; row++){
			  for (int col=0; col <columns.size() ; col++){
						//print "(${col}, ${row})";
						print "| "
						def column = columns[col];

						if ( columns[col][row] != null ){
							print   columns[col][row] ;
						} else {

						}

						if (column.size() > numRows){
							numRows = column.size();
						}

				}
				println ""
			}

	}


}
