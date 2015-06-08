package org.tzi.kodkod.model.config.impl;


/**
 * Contains the names for the configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */

public class PropertyEntry {

	/*
	 * "String" + stringValuesMin = ...
	 * "String" + stringValuesMax = ...
	 */
	public static final String stringValuesMin = "_min";
	public static final String stringValuesMax = "_max";

	/*
	 * "Real" + realValueMin = ...
	 * "Real" + realValueMax = ...
	 * "Real" + realStep = ...
	 */
	public static final String realValuesMin = "_min";
	public static final String realValuesMax = "_max";
	public static final String realStep = "_step";

	/*
	 * "Integer" + integerValueMin = ...
	 * "Integer" + integerValueMax = ...
	 */
	public static final String integerValuesMin = "_min";
	public static final String integerValuesMax = "_max";

	/*
	 * Classname + objMin = ...
	 * Classname + objMax = ...
	 */
	public static final String objMin = "_min";
	public static final String objMax = "_max";

	/*
	 * Associationname + linksMin = ...
	 * Associationname + linksMin = ...
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
	
	public static final String COMMENT_LABEL = "# ";
	public static final int PUNCHED_CARD_LENGTH = 72;
	public static final String STRONG_DIVIDE_LINE = COMMENT_LABEL + "----------------------------------------------------------------------";
	public static final String LIGHT_DIVIDE_LINE = COMMENT_LABEL + " - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
	public static final String DEFAULT_SECTION_NAME = "default";
	
	public static final String ASSOCIATIONCLASS = "_ac";
	
	public static final String INVARIANT_ACTIVE = "active";
	public static final String INVARIANT_INACTIVE = "inactive";
	public static final String INVARIANT_NEGATE = "negate";
	
}
