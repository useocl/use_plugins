package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
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

	private org.apache.commons.configuration.Configuration config;
	private Map<String, List<String[]>> classSpecificValues;
	private Map<ConfigurableType, List<String[]>> typeSpecificValues;
	private Map<ConfigurableType, TypeConfigurator> typeConfigurators;

	public PropertyConfigurationVisitor(String file) throws ConfigurationException {
		config = new PropertiesConfiguration(file);

		classSpecificValues = new HashMap<String, List<String[]>>();
		typeSpecificValues = new HashMap<ConfigurableType, List<String[]>>();
		typeConfigurators = new HashMap<ConfigurableType, TypeConfigurator>();
	}

	@Override
	public void visitModel(IModel model) {
		iterate(model.classes().iterator());

		for (IClass clazz : model.classes()) {
			iterate(clazz.attributes().iterator());
			for (IInvariant invariant : clazz.invariants()) {
				invariant.accept(this);
			}
		}
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

		List<String[]> values = new ArrayList<String[]>();
		for (String element : readSingleElements(name)) {
			element = element.trim();
			if (!(element.equals(TypeConstants.UNDEFINED) || element.equals(TypeConstants.UNDEFINED_SET))) {
				values.add(new String[] { element });
			}
		}

		classSpecificValues.put(clazz.name(), values);

		ClassConfigurator configurator = setClassConfigurator(clazz, clazz.name(), values);

		clazz.objectType().setValues(values);
		clazz.objectType().setValueSize(configurator.getMax());
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
		setAssociationConfigurator(association, readComplexElements(association.name()));
	}

	@Override
	public void visitInvariant(IInvariant invariant) {
		String name = invariant.name().replaceFirst("::", "_");
		String status = config.getString(name);
		invariant.reset();
		if (status != null) {
			if (status.equals("negate")) {
				invariant.negate();
			} else if (status.equals("inactive")) {
				invariant.deactivate();
			} else {
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
		configurator.setSpecificValues(typeSpecificValues.get(type));

		int min = 0, max = 1;
		if (type.isInteger()) {
			min = readSize(type.name() + PropertyEntry.integerValueMin, 0, true);
			max = readSize(type.name() + PropertyEntry.integerValueMax, 1, true);
		} else {
			if (type.isString()) {
				min = readSize(type.name() + PropertyEntry.stringValuesMin, 0, false);
				max = readSize(type.name() + PropertyEntry.stringValuesMax, 1, false);
			} else {
				if (type.isReal()) {
					min = readSize(type.name() + PropertyEntry.realValueMin, 0, true);
					max = readSize(type.name() + PropertyEntry.realValueMax, 1, true);
				}
			}
		}

		try {
			List<Range> ranges = new ArrayList<Range>();
			ranges.add(new Range(min, max));
			configurator.setRanges(ranges);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		type.setConfigurator(configurator);
		super.visitConfigurableType(type);
	}

	protected void setModelConfigurator(IModel model) {
		ModelConfigurator configurator;
		if (model.getConfigurator() instanceof ModelConfigurator) {
			configurator = (ModelConfigurator) model.getConfigurator();
		} else {
			configurator = new ModelConfigurator(model, new HashMap<String, IInvariant>());
			model.setConfigurator(configurator);
		}

		String status = config.getString(PropertyEntry.aggregationcyclefreeness, "off");
		if (status.equals("on")) {
			configurator.setAggregationCycleFreeness(true);
		} else {
			if (status.equals("off")) {
				configurator.setAggregationCycleFreeness(false);
			} else {
				LOG.info(LogMessages.aggregationcyclefreenessInfo());
				configurator.setAggregationCycleFreeness(DefaultConfigurationValues.aggregationcyclefreeness);
			}
		}
	}

	/**
	 * Sets the configurator for a class.
	 * 
	 * @param clazz
	 * @param name
	 * @param values
	 * @return
	 */
	protected ClassConfigurator setClassConfigurator(IClass clazz, String name, List<String[]> values) {
		ClassConfigurator configurator = new ClassConfigurator();
		configurator.setSpecificValues(values);
		int min = readSize(name + PropertyEntry.objMin, 0, false);
		configurator.setLimits(min, readSize(name + PropertyEntry.objMax, min, false));
		clazz.setConfigurator(configurator);
		return configurator;
	}

	/**
	 * Sets the configurator for an attribute.
	 * 
	 * @param attribute
	 * @param className
	 * @param specificValues
	 * @param domainValues
	 */
	protected void setAttributeConfigurator(IAttribute attribute, String className, List<String[]> specificValues, Set<String> domainValues) {
		String searchName = className + "_" + attribute.name();
		AttributeConfigurator configurator = new AttributeConfigurator(attribute);
		configurator.setSpecificValues(specificValues);
		configurator.setDomainValues(domainValues);
		configurator.setLimits(readSize(searchName + PropertyEntry.attributeDefValuesMin, 0, false),
				readSize(searchName + PropertyEntry.attributeDefValuesMax, -1, false));
		configurator.setCollectionSize(readSize(searchName + PropertyEntry.attributeColSizeMin, 0, false),
				readSize(searchName + PropertyEntry.attributeColSizeMax, -1, false));
		attribute.setConfigurator(configurator);
	}

	/**
	 * Sets a configurator for an association.
	 * 
	 * @param association
	 * @param values
	 */
	protected void setAssociationConfigurator(IAssociation association, List<String[]> values) {
		AssociationConfigurator configurator = new AssociationConfigurator();
		configurator.setSpecificValues(values);
		configurator.setLimits(readSize(association.name() + PropertyEntry.linksMin, 0, false),
				readSize(association.name() + PropertyEntry.linksMax, -1, false));
		association.setConfigurator(configurator);
	}

	/**
	 * Reads a number for the given name.
	 * 
	 * @param name
	 * @param defaultValue
	 * @param allowNegative
	 * @return
	 */
	private int readSize(String name, int defaultValue, boolean allowNegative) {
		int limit = 0;
		try {
			limit = config.getInt(name, defaultValue);
			if (!allowNegative && limit < -1) {
				limit = 0;
			}
		} catch (ConversionException e) {
			limit = defaultValue;
		}
		return limit;
	}

	/**
	 * Reads the concrete type values.
	 * 
	 * @param type
	 * @param typeName
	 */
	private void readTypeValues(Type type, String typeName) {
		List<String[]> values = getTypeSpecificValues(type);
		for (String element : readSingleElements(typeName)) {
			element = element.trim().replaceAll("'", "");
			values.add(new String[] { element });
		}
	}

	/**
	 * Reads a single value for the given name
	 * 
	 * @param name
	 * @return
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

		elements[0] = elements[0].substring(4);
		elements[elements.length - 1] = elements[elements.length - 1].replaceAll("}", "");

		return elements;
	}

	/**
	 * Reads a set of values.
	 * 
	 * @param name
	 * @return
	 */
	private List<String[]> readComplexElements(String name) {
		List<String[]> elements = new ArrayList<String[]>();

		Object property = config.getProperty(name);
		if (property != null) {
			String input = property.toString();

			input = input.substring(5, input.length() - 2);
			String[] splits = input.split("\\)");

			for (String split : splits) {
				split = split.substring(split.indexOf("(") + 1);
				String[] singleElements = split.split(",");

				String[] element = new String[singleElements.length];
				for (int i = 0; i < singleElements.length; i++) {
					element[i] = singleElements[i].trim();
				}
				elements.add(element);
			}
		}

		return elements;
	}

	/**
	 * Adds the specific values of a type.
	 * 
	 * @param type
	 * @param typeSpecificValues
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
	 * 
	 * @param type
	 * @return
	 */
	private List<String[]> getTypeSpecificValues(Type type) {
		if (typeSpecificValues.get(type) == null) {
			typeSpecificValues.put((ConfigurableType) type, new ArrayList<String[]>());
		}

		return typeSpecificValues.get(type);
	}

	/**
	 * Returns the stored specific values of a class.
	 * 
	 * @param clazz
	 * @return
	 */
	private Map<IClass, List<String[]>> getClassSpecificValues(IClass clazz) {
		Map<IClass, List<String[]>> allSpecificValues = new HashMap<IClass, List<String[]>>();
		List<String[]> values = classSpecificValues.get(clazz.name());

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
	 * 
	 * @param attribute
	 * @param owner
	 * @param specificValues
	 * @param typeSpecificValues
	 */
	private void readSpecificAttributeValues(IAttribute attribute, IClass owner, List<String[]> specificValues, Set<String[]> typeSpecificValues) {
		Map<IClass, List<String[]>> specificClassValues = getClassSpecificValues(owner);

		for (IClass clazz : getClassSpecificValues(owner).keySet()) {
			for (String[] object : specificClassValues.get(clazz)) {
				String searchName = object[0] + "_" + attribute.name();

				if (attribute.type().isSet()) {
					Set<String> values = readAttributeValues(attribute.type(), searchName, typeSpecificValues);

					for (String value : values) {
						specificValues.add(new String[] { object[0], value });
					}
				} else {
					String element = config.getString(searchName, null);
					if (element != null) {
						element = adjustElement(attribute.type(), element);
						specificValues.add(new String[] { object[0], element });

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
	 * 
	 * @param type
	 * @param readName
	 * @param typeSpecificValues
	 * @return
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
	 * 
	 * @param type
	 * @param element
	 * @return
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
}
