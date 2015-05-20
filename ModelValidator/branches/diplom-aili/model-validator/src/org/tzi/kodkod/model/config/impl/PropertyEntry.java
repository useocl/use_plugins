package org.tzi.kodkod.model.config.impl;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains the names for the configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */

public class PropertyEntry {

	/*
	 * "String" + stringValuesMin = ... "String" + stringValuesMax = ...
	 */
	public static final String stringValuesMin = "_min";
	public static final String stringValuesMax = "_max";

	/*
	 * "Real" + realValueMin = ... "Real" + realValueMax = ... "Real" + realStep
	 * = ...
	 */
	public static final String realValueMin = "_min";
	public static final String realValueMax = "_max";
	public static final String realStep = "_step";

	/*
	 * "Integer" + integerValueMin = ... "Integer" + integerValueMax = ...
	 */
	public static final String integerValueMin = "_min";
	public static final String integerValueMax = "_max";

	/*
	 * Classname + objMin = ... Classname + objMax = ...
	 */
	public static final String objMin = "_min";
	public static final String objMax = "_max";

	/*
	 * Associationname + linksMin = ... Associationname + linksMin = ...
	 */
	public static final String linksMin = "_min";
	public static final String linksMax = "_max";

	/*
	 * Classname_attributename + attributeDefValuesMin = ...
	 * Classname_attributename + attributeDefValuesMax = ... 
	 * Classname_attributename + attributeColSizeMin = ... 
	 * Classname_attributename + attributeColSizeMax = ...
	 */
	public static final String attributeDefValuesMin = "_min";
	public static final String attributeDefValuesMax = "_max";
	public static final String attributeColSizeMin = "_minSize";
	public static final String attributeColSizeMax = "_maxSize";
	
	public static final String aggregationcyclefreeness = "aggregationcyclefreeness";
	
	public static final String forbiddensharing = "forbiddensharing";
	
	public static final int PUNCHED_CARD_LENGTH = 72;
	public static final String STRONG_DIVIDE_LINE = "------------------------------------------------------------------------";
	public static final String LIGHT_DIVIDE_LINE = "-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
	public static final String DEFAULT_SECTION = "[default]";
	
	public static final String ASSOCIATIONCLASS = "_ac";
	
	public static final String INVARIANT_ACTIVE = "active";
	public static final String INVARIANT_INACTIVE = "inactive";
	public static final String INVARIANT_NEGATE = "negate";
	
	public static final Set<String> allEndings = new HashSet<String>();
	static {
		allEndings.add(stringValuesMin);
		allEndings.add(stringValuesMax);
		allEndings.add(realValueMin);
		allEndings.add(realValueMax);
		allEndings.add(realStep);
		allEndings.add(integerValueMin);
		allEndings.add(integerValueMax);
		allEndings.add(objMin);
		allEndings.add(objMax);
		allEndings.add(linksMin);
		allEndings.add(linksMax);
		allEndings.add(attributeDefValuesMin);
		allEndings.add(attributeDefValuesMax);
		allEndings.add(attributeColSizeMin);
		allEndings.add(attributeColSizeMax);
	}
}
