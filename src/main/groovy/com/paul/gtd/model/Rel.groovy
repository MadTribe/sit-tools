package com.paul.gtd.model;

	class Rel {
		def from;
		def to;
		def type = RelationsTypes.NEEDS;

		Rel(from, to){
			this.from = from;
			this.to = to;
		}

		def toJson(){
			return """   ["${this.from.id}", "${this.type.toString()}", "${this.to.id}"]""";
		}
	}

	class Context {
		def static commandOptions = [] as Set;
	}
