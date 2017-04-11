package org.tzi.use.kodkod.compare;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

public class ConfigurationComparator {

	enum Relation {
		BROADER,
		NARROWER,
		OVERLAPPING,
		DISJOINT,
		EQUAL
	}
	
	public Map<String, Relation> compare(Configuration config1, Configuration config2){
		final Map<String, Relation> result = new HashMap<>();
		
		//TODO do stuff :)
		
		return result;
	}
	
}
