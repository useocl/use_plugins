package org.tzi.kodkod.model.config.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.impl.Range;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.RealType;
import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.visitor.SimpleVisitor;

/**
 * Visitor to configure the model with the default search space and create the
 * appropriate configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class DefaultConfigurationVisitor extends SimpleVisitor {

	private static final Logger LOG = Logger.getLogger(PropertyConfigurationVisitor.class);

	private BufferedWriter writer;
	private File file;

	public DefaultConfigurationVisitor(String fileName) throws Exception {
		fileName = fileName.replaceAll("\\.use", "");
		file = new File(fileName + ".properties");
		file.createNewFile();

		writer = new BufferedWriter(new FileWriter(file));
	}

	public File getFile() {
		return file;
	}

	@Override
	public void visitModel(IModel model) {
		for (IClass clazz : model.classes()) {
			clazz.accept(this);
			iterate(clazz.attributes().iterator());
		}
		iterate(model.associations().iterator());
		iterate(model.typeFactory().configurableTypes().iterator());

		try {
			writer.close();
		} catch (IOException e) {
			LOG.error(LogMessages.propertiesConfigurationCloseError + ". " + e.getMessage());
		}
	}

	@Override
	public void visitClass(IClass clazz) {
		ClassConfigurator configurator = new ClassConfigurator();
		configurator.setLimits(DefaultConfigurationValues.objectsPerClassMin, DefaultConfigurationValues.objectsPerClassMax);
		clazz.setConfigurator(configurator);
		clazz.objectType().setValueSize(configurator.getMax());
		if (!clazz.isAbstract()) {
			write(clazz.name() + PropertyEntry.objMin, DefaultConfigurationValues.objectsPerClassMin);
			write(clazz.name() + PropertyEntry.objMax, DefaultConfigurationValues.objectsPerClassMax);
		}
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		AttributeConfigurator configurator = new AttributeConfigurator(attribute);
		configurator.setLimits(DefaultConfigurationValues.attributesPerClassMin, DefaultConfigurationValues.attributesPerClassMax);
		attribute.setConfigurator(configurator);

		String propertyName = attribute.owner().name() + "_" + attribute.name();
		write(propertyName + PropertyEntry.attributeDefValuesMin, DefaultConfigurationValues.attributesPerClassMin);
		write(propertyName + PropertyEntry.attributeDefValuesMax, DefaultConfigurationValues.attributesPerClassMax);
	}

	@Override
	public void visitAssociation(IAssociation association) {
		AssociationConfigurator configurator = new AssociationConfigurator();
		configurator.setLimits(DefaultConfigurationValues.linksPerAssocMin, DefaultConfigurationValues.linksPerAssocMax);
		association.setConfigurator(configurator);
		if (association.associationClass() == null) {
			write(association.name() + PropertyEntry.linksMin, DefaultConfigurationValues.linksPerAssocMin);
			write(association.name() + PropertyEntry.linksMax, DefaultConfigurationValues.linksPerAssocMax);
		}
	}

	@Override
	public void visitStringType(StringType stringType) {
		StringConfigurator configurator = new StringConfigurator();
		setRange(configurator, stringType.name() + PropertyEntry.stringValuesMin, DefaultConfigurationValues.stringMin, stringType.name()
				+ PropertyEntry.stringValuesMax, DefaultConfigurationValues.stringMax);
		stringType.setConfigurator(configurator);
	}

	@Override
	public void visitIntegerType(IntegerType integerType) {
		IntegerConfigurator configurator = new IntegerConfigurator();
		setRange(configurator, integerType.name() + PropertyEntry.integerValueMin, DefaultConfigurationValues.integerMin, integerType.name()
				+ PropertyEntry.integerValueMax, DefaultConfigurationValues.integerMax);
		integerType.setConfigurator(configurator);
	}

	@Override
	public void visitRealType(RealType realType) {
		RealConfigurator configurator = new RealConfigurator();
		configurator.setStep(DefaultConfigurationValues.realStep);
		setRange(configurator, realType.name() + PropertyEntry.realValueMin, DefaultConfigurationValues.realMin, realType.name()
				+ PropertyEntry.realValueMax, DefaultConfigurationValues.realMax);
		realType.setConfigurator(configurator);
		write(realType.name() + PropertyEntry.realStep, DefaultConfigurationValues.realStep);
	}

	private void setRange(TypeConfigurator configurator, String minName, int minValue, String maxName, int maxValue) {
		try {
			List<Range> ranges = new ArrayList<Range>();
			ranges.add(new Range(minValue, maxValue));
			configurator.setRanges(ranges);
			write(minName, minValue);
			write(maxName, maxValue);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Writes the value with the given name to the configuration file.
	 * 
	 * @param name
	 * @param value
	 */
	private void write(String name, int value) {
		try {
			writer.write(name + " = " + value);
			writer.newLine();
			writer.newLine();
		} catch (IOException e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
		}
	}

	/**
	 * Writes the given real value with the given name to the configuration
	 * file.
	 * 
	 * @param name
	 * @param value
	 */
	private void write(String name, double value) {
		try {
			writer.write(name + " = " + value);
			writer.newLine();
			writer.newLine();
		} catch (IOException e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
		}
	}
}
