package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationVisitor;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;

/**
 * A base class for plugins that can be configured using property files.
 * With just a file name without a sector name given, the first sector of
 * the properties file will be used. 
 * 
 * @author Frank Hilken
 */
public abstract class ConfigurablePlugin extends AbstractPlugin {

	/**
	 * Configuration of the model with the data from the given file.
	 */
	protected void configureModel(File file, PrintWriter warningsOut) throws ConfigurationException {
		HierarchicalINIConfiguration hierarchicalINIConfiguration = new HierarchicalINIConfiguration(file);
		String section;
		if (hierarchicalINIConfiguration.getSections().iterator().hasNext()) {
			section = (String) hierarchicalINIConfiguration.getSections().iterator().next();
			configureModel(hierarchicalINIConfiguration.getSection(section), warningsOut);
		} else {
			configureModel(hierarchicalINIConfiguration.getSection(null), warningsOut);
		}
	}
	
	/**
	 * Configuration of the model with the data from the given file and section.
	 */
	protected void configureModel(File file, String section, PrintWriter warningsOut) throws ConfigurationException {
		configureModel(extractConfigFromFile(file, section), warningsOut);
	}

	/**
	 * Configuration of the model with the data from the given configuration.
	 */
	protected void configureModel(Configuration config, PrintWriter warningsOut) throws ConfigurationException {
		model().reset();
		PropertyConfigurationVisitor newConfigurationVisitor = new PropertyConfigurationVisitor(config, warningsOut);
		model().accept(newConfigurationVisitor);
		
		if (newConfigurationVisitor.containErrors()) {
			throw new ConfigurationException(LogMessages.configurationError);
		}
		
		LOG.info(LogMessages.modelConfigurationSuccessful);
	}
	
	/**
	 * Configuration with the default search space.
	 */
	protected void configureModel() throws IOException {
		model().reset();
		DefaultConfigurationVisitor configurationVisitor = new DefaultConfigurationVisitor(mModel.filename());
		model().accept(configurationVisitor);

		LOG.info(LogMessages.modelConfigurationSuccessful);
	}
	
	protected Configuration extractConfigFromFile(File file, String section) throws ConfigurationException {
		HierarchicalINIConfiguration hierarchicalINIConfiguration = new HierarchicalINIConfiguration(file);
		String theSection = (section == null || hierarchicalINIConfiguration.getSections().isEmpty()) ? null : section;
		return hierarchicalINIConfiguration.getSection(theSection);
	}
	
}
