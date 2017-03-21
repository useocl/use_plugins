package org.tzi.kodkod.model.config.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.RealType;
import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.visitor.SimpleVisitor;
import org.tzi.use.kodkod.plugin.PropertiesWriter;

/**
 * Visitor to configure the model with the default search space and create the
 * appropriate configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class DefaultConfigurationVisitor extends SimpleVisitor {
	
	private static final Logger LOG = Logger.getLogger(PropertyConfigurationVisitor.class);
	private Configuration pc;
	private File file;

	public DefaultConfigurationVisitor(String fileName) throws IOException {
		fileName = fileName.replaceAll("\\.use", "");
		file = new File(fileName + ".properties");
		file.createNewFile();
		pc = new PropertiesConfiguration();
	}

	public File getFile() {
		return file;
	}

	@Override
	public void visitModel(IModel model) {
		iterate(model.typeFactory().configurableTypes().iterator());
		iterate(model.classes().iterator());
		iterate(model.associations().iterator());
		
		PropertiesWriter pw = new PropertiesWriter(model);
		pw.setIsDefaultConfiguration(true);
		try {
			pw.writeToFile(file, pc);
		} catch (IOException e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError);
		}
	}

	@Override
	public void visitClass(IClass clazz) {
		if (!clazz.isAbstract()) {
			write(clazz.name() + PropertyEntry.objMin, DefaultConfigurationValues.objectsPerClassMin);
			write(clazz.name() + PropertyEntry.objMax, DefaultConfigurationValues.objectsPerClassMax);
		}
		iterate(clazz.attributes().iterator());
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		String propertyName = attribute.owner().name() + "_" + attribute.name();
		write(propertyName + PropertyEntry.attributeDefValuesMin, DefaultConfigurationValues.attributesPerClassMin);
		write(propertyName + PropertyEntry.attributeDefValuesMax, DefaultConfigurationValues.attributesPerClassMax);
		write(propertyName + PropertyEntry.attributeColSizeMin, DefaultConfigurationValues.attributesColSizeMin);
		write(propertyName + PropertyEntry.attributeColSizeMax, DefaultConfigurationValues.attributesColSizeMax);
	}

	@Override
	public void visitAssociation(IAssociation association) {
		if (!association.isAssociationClass()) {
			write(association.name() + PropertyEntry.linksMin, DefaultConfigurationValues.linksPerAssocMin);
			write(association.name() + PropertyEntry.linksMax, DefaultConfigurationValues.linksPerAssocMax);
		}
	}

	@Override
	public void visitStringType(StringType stringType) {
		write(stringType.name() + PropertyEntry.stringValuesMin, DefaultConfigurationValues.stringMin);
		write(stringType.name() + PropertyEntry.stringValuesMax, DefaultConfigurationValues.stringMax);
	}

	@Override
	public void visitIntegerType(IntegerType integerType) {
		write(integerType.name() + PropertyEntry.integerValuesMin, DefaultConfigurationValues.integerMin);
		write(integerType.name() + PropertyEntry.integerValuesMax, DefaultConfigurationValues.integerMax);
	}

	@Override
	public void visitRealType(RealType realType) {
	}

	/**
	 * Writes the given int value with the given name to the configuration file.
	 * 
	 * @param name
	 * @param value
	 */
	private void write(String name, int value) {
		try {
			pc.setProperty(name, value);
		} catch (Exception e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
		}
	}

}
