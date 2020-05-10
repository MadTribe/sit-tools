package com.paul.gtd.model

import groovy.json.JsonOutput

public class GraphItem {
	private static int unique = 1;
	def id;
	def fullName;
	def url = null;
	def boolean deleted = false;

	def type = ItemTypes.GOAL;

	def int timeNeeded = 0;
	def boolean complete = false;
	def boolean actionable = false;
	def int relativeWorth = 1;
	def Long creationDate = null; // TODO
	def Long lastUpdate = null; // TODO



	def visitId; // transient
	def depth;	 // transient
	def worth = 0;	 // transient
	def outboundRelationships = []; // from root out (transient)
	def properties = [:]; // from root out (transient)
	def inboundRelationships = [];  // towards the root (transient)

	def fixId(xid){
		if (xid == null){
			return "nullid";
		}
		return "_" + xid.replaceAll(",","")
			 .replaceAll(" ","_")
			 .replaceAll("'","_appo_")
			 .replaceAll("\\/","_slash_")
			 .replaceAll("\\.","_dot_")
			 .replaceAll("\"","_quote_")
			 .replaceAll("-","_")
			 .replaceAll("&","_and_")
			 .replaceAll("\\(","_lbr_")
			 .replaceAll("\\)","_rbr_")
	}

	GraphItem(Map map){
			map.each { k,v -> if (this.hasProperty(k)) { this."$k" = v} }
			this.id = "X" + this.unique++ + "X" ;
			this.type = ItemTypes.valueOf(this.type);
	}



	GraphItem(String id, String fullName, String typeName, boolean complete, boolean respectId = false){
		this.type = ItemTypes.valueOf(typeName.toUpperCase());
		this.complete = complete;

		this.fullName  = fullName;
		if (!respectId){
			this.id = "X" + this.unique++ + "X";
		} else {
			this.id = id;
		}
	}

	GraphItem(id, fullName){
		this(id, fullName, "GOAL", false);
	}

	def path(){
		def  ret = fullName;
		if (this.inboundRelationships.size() > 0 ){
				ret = this.inboundRelationships[0].from.path() + "->" + ret;
		}
		return ret;
	}

	def addOutboundRelationship(child, type = RelationsTypes.NEEDS){
		def rel = new Rel(this, child);
		rel.type = type;

		this.outboundRelationships.add(rel);
		child.inboundRelationships.add(rel);

		return rel;
	}


	def toJson(){

		if (creationDate == null){
			creationDate = new Date().time;
		}
		if (lastUpdate == null){
			lastUpdate = new Date().time;
		}

		def jstring = JsonOutput.toJson([id:id,
																		 fullName:fullName,
																		 url:url,
																	 	 type:type.toString(),
																	 	 complete:complete,
																		 timeNeeded:timeNeeded,
																		 actionable:actionable,
																		 relativeWorth:relativeWorth,
																		 worth:worth,
																		 creationDate:creationDate,
																	   lastUpdate:lastUpdate]);
	  def ret = JsonOutput.prettyPrint(jstring);

		return ret;
	}

}
