package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IInvariant;
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
public class KodkodValidateCmd extends ConfigurablePlugin implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if(!pluginCommand.getSession().hasSystem()){
			LOG.error("No model loaded.");
			return;
		}
		
		initialize(pluginCommand.getSession());
		String[] arguments = pluginCommand.getCmdArgumentList();
		
		if (arguments.length >= 1) {
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
			configureModel();
			enrichModel();
			validate(createValidator());
		} catch (Exception e) {
			LOG.error(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handling of the cmd call with a path to a configuration file and
	 * the sector selected from it.
	 * 
	 * @param arguments
	 */
	protected void handleArguments(String[] arguments) {
		String fileName = arguments[0];
		String filepath = Shell.getInstance().getFilenameToOpen(fileName.trim(), false);
		File file = new File(filepath);

		if (file.exists() && file.canRead() && !file.isDirectory()) {
			if (arguments.length >= 2) {
				String section = arguments[1];
				extractConfigureAndValidate(file, section);
			} else {
				LOG.info(LogMessages.PROPERTIES_NO_CONFIGURATION_WARNING);
				extractConfigureAndValidate(file);
			}
		} else {
			LOG.error(LogMessages.fileCmdError(file));
		}
	}
	
	/**
	 * Configures the model from the first configuration section of the configuration file,
	 * extracts an object diagram and calls the model validator.
	 */
	protected final void extractConfigureAndValidate(File file) {
		extractConfigureAndValidate(file, null);
	}

	/**
	 * Configures the model from the given configuration section of the configuration file,
	 * extracts an object diagram and calls the model validator.
	 */
	protected final void extractConfigureAndValidate(File file, String section) {
		try {
			extractConfigureAndValidate(extractConfigFromFile(file, section));
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
		}
	}
	
	protected final void extractConfigureAndValidate(Configuration config) {
		try {
			StringWriter errorBuffer = new StringWriter();
			configureModel(config, new PrintWriter(errorBuffer, true));
			enrichModel();
			validate(createValidator());
			if(errorBuffer.getBuffer().length() > 0){
				LOG.warn(errorBuffer.toString());
			}
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
		}
	}
	
	/**
	 * Creates the used model validator.
	 */
	protected KodkodModelValidator createValidator() {
		return new UseKodkodModelValidator(session);
	}
	
	private void validate(KodkodModelValidator modelValidator) {
		configureInvariantSettingsFromGenerator();
		modelValidator.validate(model());
	}

	private void configureInvariantSettingsFromGenerator() {
		for(IInvariant inv : model().classInvariants()){
			MClassInvariant srcInv = mModel.getClassInvariant(inv.qualifiedName());
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
