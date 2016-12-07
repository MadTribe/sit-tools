package com.paul.gtd.model;
import groovy.json.JsonOutput;

class GraphModel {
	def transient finalGoal;
	def transient strict = false;
	def String fileName = null;
	def settings = [:];
	def items = [:]; // map of id -> Item
	def relationships = []; // List of Rel[ationship] objects
	def transient missingNodes = [] as Set;
	def toJson(){
		StringBuffer sb = new StringBuffer();
		sb << "{\n"
		def sep = "";
		sb << "\"relationships\" : [\n";
		this.relationships.forEach{ rel ->
			sb << sep;
			 sb << rel.toJson();
			 sep = ",\n";
		}
		sb << "],\n";

		sep = "";
		sb << "\"items\" : [\n";
		this.items.forEach{ key , item ->
			sb << sep;
			 sb << item.toJson();
			 sep = ",\n";
		}
		sb << "    ],\n";
		sb << "\"settings\":";
		sb << JsonOutput.prettyPrint(JsonOutput.toJson(settings));
		sb << "}\n";
		return sb.toString();
	}
}
