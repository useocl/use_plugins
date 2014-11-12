package org.tzi.use.kodkod.plugin.gui;

public class LegendEntry {
	
	public static final String INT_MINIMUM = ConfigurationConversion.INTEGER_MIN+": "+"Lower bound of integer values."; 
	public static final String INT_MAXIMUM = ConfigurationConversion.INTEGER_MAX+": "+"Upper bound of integer values."; 
	public static final String INT_VALUES = ConfigurationConversion.INTEGER_VALUES+": "+"Concrete values of type integer that need to be present in the solution.";
	
	public static final String REAL_MINIMUM = ConfigurationConversion.REAL_MIN+": "+"Lower bound of real values.";
	public static final String REAL_MAXIMUM = ConfigurationConversion.REAL_MAX+": "+"Upper bound of real values.";
	public static final String REAL_STEP = ConfigurationConversion.REAL_STEP+": "+"Step size for the interval values of real.";
	public static final String REAL_VALUES = ConfigurationConversion.REAL_VALUES+": "+"Concrete values of type integer that need to be present in the solution.";

	public static final String STRING_MINPRESENT = ConfigurationConversion.STRING_MIN+": "+"Minimum number of present string values.";
	public static final String STRING_MAXPRESENT = ConfigurationConversion.STRING_MAX+": "+"Maximum number of present string values.  If this is lower than InstanceMin then its number is taken.";
	public static final String STRING_PRESENTSTRINGS = ConfigurationConversion.STRING_VALUES+": "+"Defindes concrete string values that need to be present inside of a solution.";

	public static final String CLASS_MININSTANCES = ConfigurationConversion.CLASSES_MIN+": "+"Mininum number of instances of the class.";
	public static final String CLASS_MAXINSTANCES = ConfigurationConversion.CLASSES_MAX+": "+"Maximum number of instances of the class. If this is lower than InstanceMin then its number is taken.";
	public static final String CLASS_INSTANCENAMES = ConfigurationConversion.CLASSES_VALUES+": "+"Concrete instance names for the objects of a class that need to be present in the solution. InstanceMax is the maximum number of instance names taken.";
	
	public static final String ATTRIBUTES_MINDEFINED = ConfigurationConversion.ATTRIBUTES_MIN+": "+"Mininum number of defined values for the attribute considering all instances of the class. The value -1 forces defined attributes for all instances of the class. This setting overrides the setting MaxDefined if it's set to -1.";
	public static final String ATTRIBUTES_MAXDEFINED = ConfigurationConversion.ATTRIBUTES_MAX+": "+"Maximum number of defined values for this attribute for all instances of the class. The value -1 does not constrain this number.";
	public static final String ATTRIBUTES_MINELEMENTS = ConfigurationConversion.ATTRIBUTES_MINSIZE+": "+"Minimum number of containing elements for collection based attributes.";
	public static final String ATTRIBUTES_MAXELEMENTS = ConfigurationConversion.ATTRIBUTES_MAXSIZE+": "+"Maximum number of containing elements for collection based attributes. The value -1 does not constrain this number.";
	public static final String ATTRIBUTES_ATTRIBUTEVALUES = ConfigurationConversion.ATTRIBUTES_VALUES+": "+"Possible values for the attribute in its type.";
	
	public static final String ASSOCIATIONS_MINLINKS = ConfigurationConversion.ASSOCIATIONS_MIN+": "+"Minimum number of links for the association. Concrete links override this setting.";
	public static final String ASSOCIATIONS_MAXLINKS = ConfigurationConversion.ASSOCIATIONS_MAX+": "+"Maximum number of links for the association. Concrete links override this setting.";
	public static final String ASSOCIATIONS_PRESENTLINKS = ConfigurationConversion.ASSOCIATIONS_VALUES+": "+"Concrete links  that need to be present in the solution.";
	
}
