package com.paul.gtd.tools.jira;

public class StringFieldReader extends FieldReader{

    public StringFieldReader(){
    
    }

    public FieldValue parseValue(String source){
        def val =  new FieldValue();
        val.name = sourceName;
        val.value = source;

        return val;
    }

}
