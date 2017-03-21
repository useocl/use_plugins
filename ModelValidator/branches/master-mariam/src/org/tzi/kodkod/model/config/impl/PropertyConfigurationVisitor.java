package org.tzi.kodkod.model.config.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.KodkodQueryCache;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.impl.Range;
import org.tzi.kodkod.model.type.ConfigurableType;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.RealType;
import org.tzi.kodkod.model.type.SetType;
import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.visitor.SimpleVisitor;

/**
 * Visitor to configure the model with the data from a given configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class PropertyConfigurationVisitor extends SimpleVisitor {

	private static final Logger LOG = Logger.getLogger(PropertyConfigurationVisitor.class);

	private final Configuration config;
	private final Map<String, List<String>> classSpecificValues = new HashMap<String, List<String>>();
	private final Map<String, Integer> classMinObjects = new HashMap<String, Integer>();
	private final Map<ConfigurableType, List<String[]>> typeSpecificValues = new HashMap<ConfigurableType, List<String[]>>();
	private final Map<ConfigurableType, TypeConfigurator> typeConfigurators = new HashMap<ConfigurableType, TypeConfigurator>();
	private final List<String> warnings = new ArrayList<String>();
	private final PrintWriter warningsOut;
	private final List<String> errors = new ArrayList<String>();

	public PropertyConfigurationVisitor(Configuration c, PrintWriter warningsOut) {
		config = c;
		this.warningsOut = warningsOut;
	}
	
	public PropertyConfigurationVisitor(String file, PrintWriter warningsOut) throws ConfigurationException {
		this(new PropertiesConfiguration(file), warningsOut);
	}

	@Override
	public void visitModel(IModel model) {
		iterate(model.classes().iterator());
		iterate(model.associations().iterator());
		iterate(model.typeFactory().configurableTypes().iterator());

		setModelConfigurator(model);
	}

	@Override
	public void visitClass(IClass clazz) {
		String name = clazz.name();
		if (clazz instanceof IAssociationClass) {
			name += "_ac";
		}

		classSpecificValues.put(clazz.name(), new ArrayList<String>());
		List<String[]> values = new ArrayList<String[]>();
		for (String element : readSingleElements(name)) {
			element = element.trim();
			if (!(element.equals(TypeConstants.UNDEFINED) || element.equals(TypeConstants.UNDEFINED_SET))) {
				values.add(new String[] { element });
				classSpecificValues.get(clazz.name()).add(element);
			}
		}

		ClassConfigurator configurator = setClassConfigurator(clazz, values);
		clazz.objectType().setValues(values);
		clazz.objectType().setValueSize(configurator.getMax());
		
		iterate(clazz.attributes().iterator());
		iterate(clazz.invariants().iterator());
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		IClass owner = attribute.owner();
		String className = owner.name();

		List<String[]> specificValues = new ArrayList<String[]>();
		Set<String[]> typeSpecificValues = new HashSet<String[]>();
		Set<String> domainValues = new HashSet<String>();

		domainValues.addAll(readAttributeValues(attribute.type(), className + "_" + attribute.name(), typeSpecificValues));

		readSpecificAttributeValues(attribute, owner, specificValues, typeSpecificValues);

		setAttributeConfigurator(attribute, className, specificValues, domainValues);
		addTypeSpecificValues(attribute.type(), typeSpecificValues);
	}

	@Override
	public void visitAssociation(IAssociation association) {
		setAssociationConfigurator(association, readComplexElements(association));
	}

	@Override
	public void visitInvariant(IInvariant invariant) {
		String name = invariant.qualifiedName().replaceFirst("::", "_");
		String status = config.getString(name);
		invariant.reset();
		if (status != null) {
			if (status.equals("negate")) {
				invariant.negate();
			} else if (status.equals("inactive")) {
				invariant.deactivate();
			} else {
				if (!status.equals("active")) {
					warning(invariant.qualifiedName() + ": " + LogMessages.invariantConfigWarning(status));
				}
				invariant.activate();
			}
		}
	}

	@Override
	public void visitIntegerType(IntegerType integerType) {
		if (typeConfigurators.get(integerType) == null) {
			typeConfigurators.put(integerType, new IntegerConfigurator());
		}
		super.visitIntegerType(integerType);
	}

	@Override
	public void visitStringType(StringType stringType) {
		if (typeConfigurators.get(stringType) == null) {
			typeConfigurators.put(stringType, new StringConfigurator());
		}
		super.visitStringType(stringType);
	}

	@Override
	public void visitRealType(RealType realType) {
		if (typeConfigurators.get(realType) == null) {
			typeConfigurators.put(realType, new RealConfigurator());
		}

		double step = config.getDouble(realType.name() + PropertyEntry.realStep, 0.5);
		((RealConfigurator) typeConfigurators.get(realType)).setStep(step);

		super.visitRealType(realType);
	}

	@Override
	public void visitConfigurableType(ConfigurableType type) {
		readTypeValues(type, type.name());

		TypeConfigurator configurator = typeConfigurators.get(type);
		List<String[]> specificValues = typeSpecificValues.get(type);
		if(specificValues.size() > 0){
			configurator.setSpecificValues(specificValues);
		}

		int min;
		int max;
		int defaultMin;
		int defaultMax;
		if (type.isInteger()) {
			min = readSize(type.name() + PropertyEntry.integerValuesMin, Integer.MIN_VALUE, true);
			max = readSize(type.name() + PropertyEntry.integerValuesMax, Integer.MIN_VALUE, true);
			
			defaultMin = DefaultConfigurationValues.integerMin;
			defaultMax = DefaultConfigurationValues.integerMax;
			
			// check for values exceeding bitwidth
			int bitwidth = KodkodModelValidatorConfiguration.getInstance().bitwidth();
			
			if(!specificValues.isEmpty()){
				int maxSpecific = Integer.MIN_VALUE;
				for(String[] s : specificValues){
					int curr;
					try {
						curr = Integer.valueOf(s[0]);
					} catch(NumberFormatException ex){
						continue;
					}
					if(curr > maxSpecific){
						maxSpecific = curr;
					}
				}
				int requiredBitwidthSpecific = KodkodModelValidatorConfiguration.calculateRequiredBitwidth(maxSpecific);
				if(requiredBitwidthSpecific > bitwidth){
					warning("The configured bitwidth is too small for the specific Integer value(s). Required bitwidth: " + requiredBitwidthSpecific + " or greater.");
				}
			}
			
			if(min != Integer.MIN_VALUE){
				int requiredBitwidthMin = KodkodModelValidatorConfiguration.calculateRequiredBitwidth(min);
				
				if(requiredBitwidthMin > bitwidth){
					warning("The configured bitwidth is too small for the property Integer min value. Required bitwidth: " + requiredBitwidthMin + " or greater.");
				}
			}
			if(max != Integer.MIN_VALUE){
				int requiredBitwidthMax = KodkodModelValidatorConfiguration.calculateRequiredBitwidth(max);
				
				if(requiredBitwidthMax > bitwidth){
					warning("The configured bitwidth is too small for the property Integer max value. Required bitwidth: " + requiredBitwidthMax + " or greater.");
				}
			}
		} else if (type.isString()) {
			min = readSize(type.name() + PropertyEntry.stringValuesMin, Integer.MIN_VALUE, false);
			max = readSize(type.name() + PropertyEntry.stringValuesMax, Integer.MIN_VALUE, false);
			
			defaultMin = DefaultConfigurationValues.stringMin;
			defaultMax = DefaultConfigurationValues.stringMax;
		} else if (type.isReal()) {
			min = readSizeDouble(type.name() + PropertyEntry.realValuesMin, Integer.MIN_VALUE, true);
			max = readSizeDouble(type.name() + PropertyEntry.realValuesMax, Integer.MIN_VALUE, true);
			
			defaultMin = (int) DefaultConfigurationValues.realMin;
			defaultMax = (int) DefaultConfigurationValues.realMax;
		}
		else {
			error("Unknown Configurable Type");
			return;
		}
		
		if(min != Integer.MIN_VALUE || max != Integer.MIN_VALUE){
			if(min == Integer.MIN_VALUE){
				min = defaultMin;
			}
			if(max == Integer.MIN_VALUE){
				max = defaultMax;
			}
			
			try {
				List<Range> ranges = new ArrayList<Range>();
				ranges.add(new Range(min, max));
				configurator.setRanges(ranges);
			} catch (Exception e) {
				error(e.getMessage());
			}
		}

		type.setConfigurator(configurator);
		super.visitConfigurableType(type);
	}

	protected void setModelConfigurator(IModel model) {
		ModelConfigurator configurator;
		if (model.getConfigurator() instanceof ModelConfigurator) {
			configurator = (ModelConfigurator) model.getConfigurator();
		} else {
			configurator = new ModelConfigurator(model);
			model.setConfigurator(configurator);
		}

		String cyclefreenessState = config.getString(PropertyEntry.aggregationcyclefreeness,
				DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS ? "on" : "off" );
		if (cyclefreenessState.equals("on")) {
			configurator.setAggregationCycleFreeness(true);
		} else {
			if (cyclefreenessState.equals("off")) {
				configurator.setAggregationCycleFreeness(false);
			} else {
				LOG.info(LogMessages.aggregationcyclefreenessInfo());
				configurator.setAggregationCycleFreeness(DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS);
			}
		}
		
		String forbiddensharingState = config.getString(PropertyEntry.forbiddensharing,
				DefaultConfigurationValues.FORBIDDENSHARING ? "on" : "off");
		if (forbiddensharingState.equals("on")) {
			configurator.setForbiddensharing(true);
		} else {
			if (forbiddensharingState.equals("off")) {
				configurator.setForbiddensharing(false);
			} else {
				LOG.info(LogMessages.forbiddensharingInfo());
				configurator.setForbiddensharing(DefaultConfigurationValues.FORBIDDENSHARING);
			}
		}
	}

	/**
	 * Sets the configurator for a class.
	 */
	protected ClassConfigurator setClassConfigurator(IClass clazz, List<String[]> values) {
		ClassConfigurator configurator;
		if (KodkodQueryCache.INSTANCE.isQueryEnabled()) {
			configurator = new ClassQueryConfigurator();
		} else {
			configurator = new ClassConfigurator();
		}

		if(values.size() > 0){
			configurator.setSpecificValues(values);
		}
		int min = readSize(clazz.name() + PropertyEntry.objMin, DefaultConfigurationValues.objectsPerClassMin, false);
		classMinObjects.put(clazz.name(), min);
		configurator.setLimits(min, readSize(clazz.name() + PropertyEntry.objMax, DefaultConfigurationValues.objectsPerClassMax, false));
		clazz.setConfigurator(configurator);
		return configurator;
	}

	/**
	 * Sets the configurator for an attribute.
	 */
	protected void setAttributeConfigurator(IAttribute attribute, String className, List<String[]> specificValues, Set<String> domainValues) {
		String searchName = className + "_" + attribute.name();
		AttributeConfigurator configurator = new AttributeConfigurator(attribute);
		configurator.setSpecificValues(specificValues);
		configurator.setDomainValues(domainValues);
		configurator.setLimits(readSize(searchName + PropertyEntry.attributeDefValuesMin, DefaultConfigurationValues.attributesPerClassMin, false),
				readSize(searchName + PropertyEntry.attributeDefValuesMax, DefaultConfigurationValues.attributesPerClassMax, false));
		configurator.setCollectionSize(readSize(searchName + PropertyEntry.attributeColSizeMin, DefaultConfigurationValues.attributesColSizeMin, false),
				readSize(searchName + PropertyEntry.attributeColSizeMax, DefaultConfigurationValues.attributesColSizeMax, false));
		attribute.setConfigurator(configurator);
	}

	/**
	 * Sets a configurator for an association.
	 */
	protected void setAssociationConfigurator(IAssociation association, List<String[]> values) {
		AssociationConfigurator configurator = new AssociationConfigurator();
		if(values.size() > 0){
			configurator.setSpecificValues(values);
		}
		configurator.setLimits(readSize(association.name() + PropertyEntry.linksMin, DefaultConfigurationValues.linksPerAssocMin, false),
				readSize(association.name() + PropertyEntry.linksMax, DefaultConfigurationValues.linksPerAssocMax, false));
		association.setConfigurator(configurator);
	}

	/**
	 * Reads a number for the given name.
	 */
	private int readSize(String name, int errorValue, boolean allowNegative) {
		int limit = 0;
		try {
			limit = config.getInt(name);
			if (!allowNegative && limit < -1) {
				limit = 0;
			}
		} catch (ConversionException e) {
			warning(name + ": " + LogMessages.sizeConfigWarning(name));
			limit = errorValue;
		} catch (NoSuchElementException e) {
			limit = errorValue;
		}
		return limit;
	}
	
	private int readSizeDouble(String name, int errorValue, boolean allowNegative) {
		int limit = 0;
		try {
			limit = (int) Math.round(config.getDouble(name));
			if (!allowNegative && limit < -1) {
				limit = 0;
			}
		} catch (ConversionException e) {
			warning(name + ": " + LogMessages.sizeConfigWarning(name));
			limit = errorValue;
		} catch (NoSuchElementException e) {
			limit = errorValue;
		}
		return limit;
	}

	/**
	 * Reads the concrete type values.
	 */
	private void readTypeValues(Type type, String typeName) {
		List<String[]> values = getTypeSpecificValues(type);
		for (String element : readSingleElements(typeName)) {
			element = element.trim().replaceAll("'", "");
			if(!element.isEmpty()){
				values.add(new String[] { element });
			}
		}
	}

	/**
	 * Reads a single value for the given name
	 */
	private String[] readSingleElements(String name) {
		String[] elements = config.getStringArray(name);
		if (elements.length == 0) {
			return elements;
		} else {
			if (elements.length == 1 && elements[0].equals(TypeConstants.UNDEFINED_SET)) {
				return new String[] { TypeConstants.UNDEFINED_SET };
			}
		}

		checkSetSyntax(elements[0], elements[elements.length - 1], name, "Set{a,b,c}");

		elements[0] = elements[0].substring(4);
		elements[elements.length - 1] = elements[elements.length - 1].replaceAll("}", "");

		return elements;
	}

	/**
	 * Reads a set of values.
	 */
	private List<String[]> readComplexElements(IAssociation association) {
		String name = association.name();
		List<String[]> elements = new ArrayList<String[]>();

		Object property = config.getProperty(name);
		if (property != null) {
			String input = property.toString();
			input = input.substring(1, input.length() - 1);

			checkSetSyntax(input, input, name, "Set{(a1,b1),(a2,b2)}");

			input = input.substring(4, input.length() - 1);
			String[] splits = input.split("\\)");

			for (String split : splits) {
				split = split.substring(split.indexOf("(") + 1);
				String[] singleElements = split.split(",");

				int j = 0;
				String[] element = new String[singleElements.length];
				for (int i = 0; i < singleElements.length; i++) {
					element[i] = singleElements[i].trim();

					String className;
					if (i == 0 && association.associationClass() != null) {
						className = association.associationClass().name();
					} else {
						className = association.associationEnds().get(j).associatedClass().name();
						j++;
					}

					checkComplexElement(name, split, element[i], className);
				}
				elements.add(element);
			}
		}

		return elements;
	}

	private void checkComplexElement(String name, String input, String element, String className) {
		if (!classSpecificValues.get(className).contains(element)) {

			if (element.startsWith(className.toLowerCase())) {
				String number = element.substring(className.length());
				try {
					int nbr = Integer.parseInt(number);
					if (!(nbr > classSpecificValues.get(className).size() && nbr <= classMinObjects.get(className))) {
						complexElementError(name,input, element, className);
					}
				} catch (NumberFormatException e) {
					complexElementError(name,input, element, className);
				}
			} else {
				complexElementError(name,input, element, className);
			}
		}
	}

	private void complexElementError(String name, String input, String element, String className) {
		String error = name + ": (" + input + ") at element " + element + ": " + LogMessages.complexElementConfigError(className);
		error(error);
	}

	private void checkSetSyntax(String start, String end, String name, String syntax) {
		if (!start.startsWith("Set{") || !end.endsWith("}")) {
			error(name + ": " + LogMessages.setSyntaxConfigError(syntax));
		}
	}

	/**
	 * Adds the specific values of a type.
	 */
	private void addTypeSpecificValues(Type type, Collection<String[]> typeSpecificValues) {
		if (type.isSet()) {
			type = ((SetType) type).elemType();
		}

		if (type instanceof ConfigurableType) {
			List<String[]> values = getTypeSpecificValues(type);

			if (type.isString() || type.isReal()) {
				for (String[] value : typeSpecificValues) {
					values.add(new String[] { value[0] });
				}
			} else if (type.isInteger()) {
				values.addAll(typeSpecificValues);
			}
		}
	}

	/**
	 * Returns the stored specific values of a type.
	 */
	private List<String[]> getTypeSpecificValues(Type type) {
		if (typeSpecificValues.get(type) == null) {
			typeSpecificValues.put((ConfigurableType) type, new ArrayList<String[]>());
		}

		return typeSpecificValues.get(type);
	}

	/**
	 * Returns the stored specific values of a class.
	 */
	private Map<IClass, List<String>> getClassSpecificValues(IClass clazz) {
		Map<IClass, List<String>> allSpecificValues = new HashMap<IClass, List<String>>();
		List<String> values = classSpecificValues.get(clazz.name());

		if (values != null) {
			allSpecificValues.put(clazz, values);
		}
		for (IClass child : clazz.children()) {
			allSpecificValues.putAll(getClassSpecificValues(child));
		}
		return allSpecificValues;
	}

	/**
	 * Reads the specific values for an attribute.
	 */
	private void readSpecificAttributeValues(IAttribute attribute, IClass owner, List<String[]> specificValues, Set<String[]> typeSpecificValues) {
		Map<IClass, List<String>> specificClassValues = getClassSpecificValues(owner);

		for (IClass clazz : specificClassValues.keySet()) {
			for (String object : specificClassValues.get(clazz)) {
				String searchName = object + "_" + attribute.name();

				if (attribute.type().isSet()) {
					Set<String> values = readAttributeValues(attribute.type(), searchName, typeSpecificValues);

					for (String value : values) {
						specificValues.add(new String[] { object, value });
					}
				} else {
					String element = config.getString(searchName, null);
					if (element != null) {
						element = adjustElement(attribute.type(), element);
						specificValues.add(new String[] { object, element });

						if (!(element.equals(TypeConstants.UNDEFINED) || element.equals(TypeConstants.UNDEFINED_SET))) {
							typeSpecificValues.add(new String[] { element });
						}
					}
				}
			}
		}
	}

	/**
	 * Reads the values for an attribute.
	 */
	private Set<String> readAttributeValues(Type type, String readName, Set<String[]> typeSpecificValues) {
		Set<String> values = new HashSet<String>();
		for (String element : readSingleElements(readName)) {
			element = adjustElement(type, element);

			values.add(element);
			if (!(element.equals(TypeConstants.UNDEFINED) || element.equals(TypeConstants.UNDEFINED_SET))) {
				typeSpecificValues.add(new String[] { element });
			}
		}
		return values;
	}

	/**
	 * Adjust an element.
	 */
	private String adjustElement(Type type, String element) {
		element = element.trim();
		if (element.equals(TypeConstants.UNDEFINED) || element.equals(TypeConstants.UNDEFINED_SET)) {
			return element;
		}

		if (type.isCollection()) {
			type = ((SetType) type).elemType();
		}

		if (type.isString()) {
			element = element.replaceAll("'", "");
		}
		return element;
	}
	
	public boolean containErrors(){
		return !errors.isEmpty();
	}
	
	private void warning(String warning){
		if(warnings.isEmpty()){
			warningsOut.write("Warnings during the configuration:");
		}
		warningsOut.write("\n" + warning);
		warnings.add(warning);
		LOG.warn(warning);
	}
	
	private void error(String error){
		errors.add(error);
		LOG.error(error);
	}
}
