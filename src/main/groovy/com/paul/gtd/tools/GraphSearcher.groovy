package com.paul.gtd.tools;
import static com.paul.gtd.tools.Time.*;
import com.paul.gtd.model.*;

public class GraphSearcher {
		def GraphModel model;

		def normalize(input){
				return input.toLowerCase();
		}

		def search(String term){
			def results = [];
			term = normalize(term);
			model.items.each{ id, value ->
					if (normalize(value.fullName).indexOf(term) != -1){
						results << value;

					}
			}
			return results;
		}

}
