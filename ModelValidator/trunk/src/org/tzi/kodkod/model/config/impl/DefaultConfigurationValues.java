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

	public static final int stringMin = 1;
	public static final int stringMax = 5;

	public static final int integerMin = -10;
	public static final int integerMax = 10;

	public static final int realMin = -2;
	public static final int realMax = 2;
	public static final double realStep = 0.5;
	
	public static final boolean aggregationcyclefreeness = false;
	
	public static final boolean forbiddensharing=true;
}
