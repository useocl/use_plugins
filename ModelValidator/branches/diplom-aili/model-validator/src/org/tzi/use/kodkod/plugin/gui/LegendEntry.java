package org.tzi.use.kodkod.plugin.gui;

public class LegendEntry {
	
	public static final String INT_MINIMUM = ConfigurationTerms.INTEGER_MIN+": "+"Lower bound of integer values."; 
	public static final String INT_MAXIMUM = ConfigurationTerms.INTEGER_MAX+": "+"Upper bound of integer values."; 
	public static final String INT_VALUES = ConfigurationTerms.INTEGER_VALUES+": "+"Concrete values of type integer that need to be present in the solution.";
	
	public static final String REAL_MINIMUM = ConfigurationTerms.REAL_MIN+": "+"Lower bound of real values.";
	public static final String REAL_MAXIMUM = ConfigurationTerms.REAL_MAX+": "+"Upper bound of real values.";
	public static final String REAL_STEP = ConfigurationTerms.REAL_STEP+": "+"Step size for the interval values of real.";
	public static final String REAL_VALUES = ConfigurationTerms.REAL_VALUES+": "+"Concrete values of type integer that need to be present in the solution.";

	public static final String STRING_MINPRESENT = ConfigurationTerms.STRING_MIN+": "+"Minimum number of present string values.";
	public static final String STRING_MAXPRESENT = ConfigurationTerms.STRING_MAX+": "+"Maximum number of present string values.  If this is lower than InstanceMin then its number is taken.";
	public static final String STRING_PRESENTSTRINGS = ConfigurationTerms.STRING_VALUES+": "+"Defindes concrete string values that need to be present inside of a solution.";

	public static final String CLASS_MININSTANCES = ConfigurationTerms.CLASSES_MIN+": "+"Mininum number of instances of the class.";
	public static final String CLASS_MAXINSTANCES = ConfigurationTerms.CLASSES_MAX+": "+"Maximum number of instances of the class. If this is lower than InstanceMin then its number is taken.";
	public static final String CLASS_INSTANCENAMES = ConfigurationTerms.CLASSES_VALUES+": "+"Concrete instance names for the objects of a class that need to be present in the solution. InstanceMax is the maximum number of instance names taken.";
	
	public static final String ATTRIBUTES_MINDEFINED = ConfigurationTerms.ATTRIBUTES_MIN+": "+"Mininum number of defined values for the attribute considering all instances of the class. The value -1 forces defined attributes for all instances of the class. This setting overrides the setting MaxDefined if it's set to -1.";
	public static final String ATTRIBUTES_MAXDEFINED = ConfigurationTerms.ATTRIBUTES_MAX+": "+"Maximum number of defined values for this attribute for all instances of the class. The value -1 does not constrain this number.";
	public static final String ATTRIBUTES_MINELEMENTS = ConfigurationTerms.ATTRIBUTES_MINSIZE+": "+"Minimum number of containing elements for collection based attributes.";
	public static final String ATTRIBUTES_MAXELEMENTS = ConfigurationTerms.ATTRIBUTES_MAXSIZE+": "+"Maximum number of containing elements for collection based attributes. The value -1 does not constrain this number.";
	public static final String ATTRIBUTES_ATTRIBUTEVALUES = ConfigurationTerms.ATTRIBUTES_VALUES+": "+"Possible values for the attribute in its type.";
	
	public static final String ASSOCIATIONS_MINLINKS = ConfigurationTerms.ASSOCIATIONS_MIN+": "+"Minimum number of links for the association. Concrete links override this setting.";
	public static final String ASSOCIATIONS_MAXLINKS = ConfigurationTerms.ASSOCIATIONS_MAX+": "+"Maximum number of links for the association. Concrete links override this setting.";
	public static final String ASSOCIATIONS_PRESENTLINKS = ConfigurationTerms.ASSOCIATIONS_VALUES+": "+"Concrete links  that need to be present in the solution.";
	
	public static final String LEGEND = 
			"Legend:\n"
			+"\n"
			+ConfigurationTerms.INTEGER_MIN+": Lower bound of integer or real values. \n\n"
			+ConfigurationTerms.INTEGER_MAX+": Upper bound of integer or real values. \n\n"
			+ConfigurationTerms.INTEGER_VALUES+": Concrete values of type integer or real that are need to be present in the solution."+"\n\n"
			+CLASS_MININSTANCES+"\n\n"
			+CLASS_MAXINSTANCES+"\n\n"
			+CLASS_INSTANCENAMES+"\n\n"
			+ATTRIBUTES_MINDEFINED+"\n\n"
			+ATTRIBUTES_MAXDEFINED+"\n\n"
			+ATTRIBUTES_MINELEMENTS+"\n\n"
			+ATTRIBUTES_MAXELEMENTS+"\n\n"
			+ATTRIBUTES_ATTRIBUTEVALUES+"\n\n"
			+ASSOCIATIONS_MINLINKS+"\n\n"
			+ASSOCIATIONS_MAXLINKS+"\n\n"
			+ASSOCIATIONS_PRESENTLINKS;
	
}
