package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationVisitor;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.kodkod.UseDefaultConfigKodkodModelValidator;
import org.tzi.use.kodkod.UseKodkodModelValidator;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.uml.mm.MClassInvariant;

/**
 * Cmd-Class for a simple model validation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodValidateCmd extends AbstractPlugin implements IPluginShellCmdDelegate {

	private PropertyConfigurationVisitor configurationVisitor;

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if(!pluginCommand.getSession().hasSystem()){
			LOG.error("No model loaded.");
			return;
		}
		
		initialize(pluginCommand.getSession());

		String arguments = pluginCommand.getCmdArguments();
		if (arguments.length() > 1) {
			handleArguments(arguments);
		} else {
			noArguments();
		}
	}

	/**
	 * Handling if no path to a configuration file is given
	 */
	protected void noArguments() {
		try {
			File file = configureModel();
			enrichModel();
			validate(new UseDefaultConfigKodkodModelValidator(session, file));
		} catch (Exception e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handling of the cmd call with a path to a configuration file.
	 * 
	 * @param arguments
	 */
	protected void handleArguments(String arguments) {
		File file = new File(Shell.getInstance().getFilenameToOpen(arguments.trim(), false) );

		if (file.exists() && file.canRead() && !file.isDirectory()) {
			extractConfigureAndValidate(file);
		} else {
			LOG.error(LogMessages.fileCmdError(file));
		}
	}

	/**
	 * Configures the model, extracts an object diagram and calls the model
	 * validator.
	 * 
	 * @param file
	 */
	protected final void extractConfigureAndValidate(File file) {
		try {
			configureModel(file);
			enrichModel();
			validate(createValidator());
			configurationVisitor.printWarnings();
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
		}
	}

	/**
	 * Creates the used model validator.
	 * 
	 * @return
	 */
	protected KodkodModelValidator createValidator() {
		return new UseKodkodModelValidator(session);
	}

	/**
	 * Configuration of the model with the data from the given file.
	 * 
	 * @param file
	 * @throws ConfigurationException
	 */
	private void configureModel(File file) throws ConfigurationException {
		model().reset();
		configurationVisitor = new PropertyConfigurationVisitor(file.getAbsolutePath());
		model().accept(configurationVisitor);

		if (configurationVisitor.containErrors()) {
			throw new ConfigurationException();
		}

		LOG.info(LogMessages.modelConfigurationSuccessful);
	}

	/**
	 * Configuration with the default search space.
	 * 
	 * @return
	 * @throws Exception
	 */
	private File configureModel() throws Exception {
		model().reset();
		DefaultConfigurationVisitor configurationVisitor = new DefaultConfigurationVisitor(mModel.filename());
		model().accept(configurationVisitor);

		LOG.info(LogMessages.modelConfigurationSuccessful);

		return configurationVisitor.getFile();
	}

	private void validate(KodkodModelValidator modelValidator) {
		configureInvariantSettingsFromGenerator();
		modelValidator.validate(model());
	}

	private void configureInvariantSettingsFromGenerator() {
		for(IInvariant inv : model().classInvariants()){
			MClassInvariant srcInv = mModel.getClassInvariant(inv.name());
			if(!srcInv.isActive() && inv.isActivated()){
				inv.deactivate();
				LOG.info(LogMessages.flagChangeInfo(inv, true));
				continue;
			}
			
			if(srcInv.isNegated() && !inv.isNegated()){
				inv.negate();
				LOG.info(LogMessages.flagChangeInfo(inv, false));
				continue;
			}
		}
	}
}
