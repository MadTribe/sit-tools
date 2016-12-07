package com.paul.gtd.tools;

public class Time {
   static def hoursToDuration(int hours){
  		def ret = "" + hours + "hrs";
  		def days = hours / 8;

  		if (days > 1){
  			ret = Math.ceil(days) + "days";
  		}

  		return ret;
  	}

}
