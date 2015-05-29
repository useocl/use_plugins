package org.tzi.use.kodkod.plugin.gui;

public class LegendEntry {
	
	public static final String INT_MINIMUM = ConfigurationTerms.INTEGER_MIN+": "
			+ "Minimum integer value. This affects all integer attributes."; 
	public static final String INT_MAXIMUM = ConfigurationTerms.INTEGER_MAX+": "
			+ "Maximum integer value. This affects all integer attributes."; 
	public static final String INT_VALUES = ConfigurationTerms.INTEGER_VALUES+": "
			+ "Integer values required in the search space for the solution. Example: 1,3";
	
	public static final String REAL_MINIMUM = ConfigurationTerms.REAL_MIN+": "
			+ "Minimum real value. This affects all real attributes.";
	public static final String REAL_MAXIMUM = ConfigurationTerms.REAL_MAX+": "
			+ "Maximum real value. This affects all real attributes.";
	public static final String REAL_STEP = ConfigurationTerms.REAL_STEP+": "
			+ "Step range between generated real values.";
	public static final String REAL_VALUES = ConfigurationTerms.REAL_VALUES+": "
			+ "Real values required in the search space for the solution. Example: 1.2,3.1";

	public static final String STRING_MINPRESENT = ConfigurationTerms.STRING_MIN+": "
			+ "Minimum divers string values.";
	public static final String STRING_MAXPRESENT = ConfigurationTerms.STRING_MAX+": "
			+ "Maximum divers string values.";
	public static final String STRING_PRESENTSTRINGS = ConfigurationTerms.STRING_VALUES+": "
			+ "Preferred string values in the search space for the solution. Example: 'ada','bob'";

	public static final String CLASS_MININSTANCES = ConfigurationTerms.CLASSES_MIN+": "
			+ "Mininum quantity of instances of this class. Overrides the maximum if it's lower.";
	public static final String CLASS_MAXINSTANCES = ConfigurationTerms.CLASSES_MAX+": "
			+ "Maximum quantity of instances of this class. If it's lower than the maximum then its number is taken.";
	public static final String CLASS_INSTANCENAMES = ConfigurationTerms.CLASSES_VALUES+": "
			+ "Preferred class instance identities in the search space for the solution. Example: ada, bob";
	
	public static final String ATTRIBUTES_MINDEFINED = ConfigurationTerms.ATTRIBUTES_MIN+": "
			+ "Mininum objects with defined attribute in the solution. "
			+ "If the value is -1 then all objects of its class are required to have the attribute defined. "
			+ "This setting overrides the setting for maximum defined object attributes, if the maximum is set to -1.";
	public static final String ATTRIBUTES_MAXDEFINED = ConfigurationTerms.ATTRIBUTES_MAX+": "
			+ "Maximum objects with defined attribute in the solution. "
			+ "If the value is -1 then the objects attributes defined is not limited.";
	public static final String ATTRIBUTES_MINELEMENTS = ConfigurationTerms.ATTRIBUTES_MINSIZE+": "
			+ "Minimum count of elements in attributes that are collection based.";
	public static final String ATTRIBUTES_MAXELEMENTS = ConfigurationTerms.ATTRIBUTES_MAXSIZE+": "
			+ "Maximum count of elements in attributes that are collection based. "
			+ "The value -1 does not constrain this number.";
	public static final String ATTRIBUTES_ATTRIBUTEVALUES = ConfigurationTerms.ATTRIBUTES_VALUES+": "
			+ "Possible values in the type of the attribute.";
	
	public static final String ASSOCIATIONS_MINLINKS = ConfigurationTerms.ASSOCIATIONS_MIN+": "
			+ "Minimum count of links of the association. Predefined links are included.";
	public static final String ASSOCIATIONS_MAXLINKS = ConfigurationTerms.ASSOCIATIONS_MAX+": "
			+ "Maximum count of links of the association. Predefined links are included.";
	public static final String ASSOCIATIONS_PRESENTLINKS = ConfigurationTerms.ASSOCIATIONS_VALUES+": "
			+ "Predefined links required in the search space for the solution. Example: (ada,bob),(cyd,dan)";
	
	public static final String LEGEND = 
			"Legend:\n"
			+"\n"
			+ConfigurationTerms.INTEGER_MIN+": Minimum integer or real value. "
					+ "This affects all attributes these types. \n\n"
			+ConfigurationTerms.INTEGER_MAX+": Maximum integer or real value. "
					+ "This affects all attributes these types. \n\n"
			+ConfigurationTerms.INTEGER_VALUES+": Values required in the search space for the solution. Example: 2.1,5.7"+"\n\n"
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
