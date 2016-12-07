package com.paul.gtd.tools;

public class Display {
   static def prints(msg){
  		println msg;
   }

   static def getInput(msg){
  	   def option = System.console().readLine " ${msg} ? ";
       return option;
   }

   static def getNumber(msg){
      def option = getInput(msg);
      try {
        return Integer.valueOf(option);
      } catch (Exception e){
        return null;
      }
   }

}
