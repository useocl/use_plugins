package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationVisitor;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;

/**
 * A base class for plugins that can be configured using property files.
 * 
 * @author Frank Hilken
 */
public abstract class ConfigurablePlugin extends AbstractPlugin {

	private PropertyConfigurationVisitor configurationVisitor;
	
	/**
	 * Configuration of the model with the data from the given file.
	 * 
	 * @param file
	 * @throws ConfigurationException
	 */
	protected PropertyConfigurationVisitor configureModel(File file) throws ConfigurationException {
		model().reset();
		configurationVisitor = new PropertyConfigurationVisitor(file.getAbsolutePath());
		model().accept(configurationVisitor);

		if (configurationVisitor.containErrors()) {
			throw new ConfigurationException();
		}

		LOG.info(LogMessages.modelConfigurationSuccessful);
		return configurationVisitor;
	}

	/**
	 * Configuration with the default search space.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected File configureModel() throws Exception {
		model().reset();
		DefaultConfigurationVisitor configurationVisitor = new DefaultConfigurationVisitor(mModel.filename());
		model().accept(configurationVisitor);

		LOG.info(LogMessages.modelConfigurationSuccessful);

		return configurationVisitor.getFile();
	}
	
}
