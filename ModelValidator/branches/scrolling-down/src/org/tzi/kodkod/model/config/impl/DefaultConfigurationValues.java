package org.tzi.kodkod.model.config.impl;

/**
 * Contains the values of the default search space.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class DefaultConfigurationValues {

	public static final int objectsPerClassMin = 1;
	public static final int objectsPerClassMax = 1;

	public static final int linksPerAssocMin = 1;
	public static final int linksPerAssocMax = 1;

	public static final int attributesPerClassMin = -1;
	public static final int attributesPerClassMax = -1;
	
	public static final int attributesColSizeMin = 0;
	public static final int attributesColSizeMax = -1;

	public static final int stringMin = 0;
	public static final int stringMax = 10;
	public static final boolean stringEnabled = false;

	public static final int integerMin = -10;
	public static final int integerMax = 10;
	public static final boolean integerEnabled = true;

	public static final double realMin = -2d;
	public static final double realMax = 2d;
	public static final double realStep = 0.5d;
	public static final boolean realEnabled = false;
	
	public static final boolean AGGREGATIONCYCLEFREENESS = false;
	public static final boolean FORBIDDENSHARING = true;
	
	public static final boolean INVARIANT_ACTIVE = true;
	public static final boolean INVARIANT_NEGATE = false;
}
