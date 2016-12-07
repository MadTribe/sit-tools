package com.paul.gtd;
import com.paul.gtd.tools.*;
import static com.paul.gtd.tools.Display.*;
import com.paul.gtd.tools.jira.*;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class HSBCEngineeringAnalysis {

		public static void main(String[] args){
			if (args.length > 0){
				def inputFileName = args[0];
				def outFile = inputFileName + ".json";
				def issues = new JiraExportReader().readExport(inputFileName)
				def reader = new DataReader();
				def tools = new SituationTools();
				def data = tools.init(outFile);

			  issues.each{ issue ->
						
				}

			} else {
				prints "jimport <fileName>"
			}
		}


}
