package com.paul.gtd.tools.jira;

public abstract class FieldReader{
    String name;

    def parseName(name){

      return name.replaceAll(",","")
         .replaceAll(" ","_")
         .replaceAll("Σ","_sum_")

    }
    public abstract FieldValue parseValue(String source);
    public String toString(){
      name;
    }

}
