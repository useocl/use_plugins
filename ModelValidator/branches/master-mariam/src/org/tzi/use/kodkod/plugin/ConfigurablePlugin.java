package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.io.FileReader;
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
	protected void configureModel(PrintWriter warningsOut) throws IOException, ConfigurationException {
		model().reset();
		DefaultConfigurationVisitor configurationVisitor = new DefaultConfigurationVisitor(mModel.filename());
		model().accept(configurationVisitor);
		configureModel(extractConfigFromFile(configurationVisitor.getFile()), warningsOut);
		
		LOG.info(LogMessages.modelConfigurationSuccessful);
	}
	
	protected Configuration extractConfigFromFile(File file) throws ConfigurationException {
		HierarchicalINIConfiguration hierarchicalINIConfiguration = readConfiguration(file);
		if(hierarchicalINIConfiguration.getSections().isEmpty()){
			return hierarchicalINIConfiguration.getSection(null);
		} else {
			String section = hierarchicalINIConfiguration.getSections().iterator().next();
			return hierarchicalINIConfiguration.getSection(section);
		}
	}
	
	protected Configuration extractConfigFromFile(File file, String section) throws ConfigurationException {
		HierarchicalINIConfiguration hierarchicalINIConfiguration = readConfiguration(file);
		if(!hierarchicalINIConfiguration.getSections().contains(section)){
			if(section == null){
				String sName = hierarchicalINIConfiguration.getSections().iterator().next();
				return hierarchicalINIConfiguration.getSection(sName);
			} else {
				throw new ConfigurationException("Selected section does not exist in properties file.");
			}
		}
		
		return hierarchicalINIConfiguration.getSection(section);
	}
	
	private HierarchicalINIConfiguration readConfiguration(File f) throws ConfigurationException {
		HierarchicalINIConfiguration hierarchicalINIConfiguration = new HierarchicalINIConfiguration();
		try {
			hierarchicalINIConfiguration.load(new USECommentFilterReader(new FileReader(f)));
		} catch (IOException ex) {
			throw new ConfigurationException(ex);
		}
		return hierarchicalINIConfiguration;
	}
	
}
