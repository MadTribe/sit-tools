 package com.paul.gtd.tools.jira;

public class FieldMapping {

    def fieldName = [];
    def fieldReader = [];
    def fieldReaderLookup = [:];

    def handleString = { String value -> return value;}
    def handleIdList = { String value -> return value.replaceAll(" ","").split(",");}

    public FieldMapping(){

      fieldReaderLookup["Project"] = handleString;
      fieldReaderLookup["Key"] = handleString;
      fieldReaderLookup["Summary"] = handleString;
      fieldReaderLookup["Issue Type"] = handleString;
      fieldReaderLookup["Status"] = handleString;
      fieldReaderLookup["Priority"] = handleString;
      fieldReaderLookup["Resolution"] = handleString;
      fieldReaderLookup["Assignee"] = handleString;
    	fieldReaderLookup["Reporter"] = handleString;
      fieldReaderLookup["Creator"] = handleString;
      fieldReaderLookup["Created"] = handleString;
      fieldReaderLookup["Last Viewed"] = handleString;
      fieldReaderLookup["Updated	Resolved"] = handleString;
      fieldReaderLookup["Affects Version/s"] = handleString;
      fieldReaderLookup["Fix Version/s"] = handleString;
      fieldReaderLookup["Component/s"] = handleString;
      fieldReaderLookup["Due Date"] = handleString;
      fieldReaderLookup["Votes"] = handleString;
      fieldReaderLookup["Watchers"] = handleString;
      fieldReaderLookup["Images"] = handleString;
      fieldReaderLookup["Original Estimate"] = handleString;
      fieldReaderLookup["Remaining Estimate"] = handleString;
      fieldReaderLookup["Time Spent"] = handleString;
      fieldReaderLookup["Work Ratio"] = handleString;
      fieldReaderLookup["Sub-Tasks"] = handleString;
      fieldReaderLookup["Linked Issues"] = handleIdList;
      fieldReaderLookup["Environment"] = handleString;
      fieldReaderLookup["Description"] = handleString;
      fieldReaderLookup["Security Level"] = handleString;
      fieldReaderLookup["Progress"] = handleString;
      fieldReaderLookup["Σ Progress"] = handleString;
      fieldReaderLookup["Σ Time Spent"] = handleString;
      fieldReaderLookup["Σ Remaining Estimate"] = handleString;
      fieldReaderLookup["Σ Original Estimate"] = handleString;
      fieldReaderLookup["Labels"] = handleIdList;
      fieldReaderLookup["Epic"] = handleString;
      fieldReaderLookup["Colour"] = handleString;
      fieldReaderLookup["Story Points"] = handleString;
      fieldReaderLookup["Implementation Date"] = handleString;
      fieldReaderLookup["Implementation Date"] = handleString;
      fieldReaderLookup["Flagged	Rank"] = handleString;
      fieldReaderLookup["Additional Assignee"] = handleString;
      fieldReaderLookup["Sprint"] = handleString;
      fieldReaderLookup["Epic Link"] = handleString;
      fieldReaderLookup["Epic Status"] = handleString;
      fieldReaderLookup["GSD Change Order"] = handleString;
      fieldReaderLookup["Epic Name"] = handleString;

    }

    def configFieldReader(int idx, String name){
        fieldReader[idx] = getFieldReader(name);
        fieldName[idx] = name;
    }

    def getFieldReader(String fieldName){
        def reader = fieldReaderLookup[fieldName];
        if (reader == null){
           reader = handleString;
        }
        return reader;
    }



}
