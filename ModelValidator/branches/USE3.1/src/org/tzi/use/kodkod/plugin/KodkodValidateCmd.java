package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationVisitor;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;
import org.tzi.use.kodkod.UseDefaultConfigKodkodModelValidator;
import org.tzi.use.kodkod.UseKodkodModelValidator;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.Log;

/**
 * Cmd-Class for a simple model validation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodValidateCmd extends AbstractPlugin implements IPluginShellCmdDelegate {

	protected PrintWriter out = new PrintWriter(Log.out());
	
	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
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
			objDiagramExtraction(out);
			validate(new UseDefaultConfigKodkodModelValidator(mSystem, file, out));
		} catch (Exception e) {
			out.println(LogMessages.propertiesConfigurationCreateError + ". " + e.getMessage());
		}
	}

	/**
	 * Handling of the cmd call with a path to a configuration file.
	 * 
	 * @param arguments
	 */
	protected void handleArguments(String arguments) {
		File file = new File(arguments.substring(1));

		if (file.exists() && file.canRead() && !file.isDirectory()) {
			extractConfigureAndValidate(file);
		} else {
			out.println(LogMessages.fileCmdError(file));
		}
	}

	/**
	 * Configures the model, extracts an object diagram and calls the model
	 * validator.
	 * 
	 * @param file
	 */
	protected void extractConfigureAndValidate(File file) {
		try {
			configureModel(file);
			objDiagramExtraction(out);
			validate(createValidator());
		} catch (ConfigurationException e) {
			out.println(LogMessages.propertiesConfigurationReadError + ". " + e.getMessage());
		}
	}

	/**
	 * Creates the used model validator.
	 * 
	 * @return
	 */
	protected KodkodModelValidator createValidator() {
		return new UseKodkodModelValidator(mSystem, out);
	}

	/**
	 * Configuration of the model with the data from the given file.
	 * 
	 * @param file
	 * @throws ConfigurationException
	 */
	private void configureModel(File file) throws ConfigurationException {
		PropertyConfigurationVisitor configurationVisitor = new PropertyConfigurationVisitor(file.getAbsolutePath(), out);
		model(out).accept(configurationVisitor);

		out.println(LogMessages.modelConfigurationSuccessful);
	}

	/**
	 * Configuration with the default search space.
	 * 
	 * @return
	 * @throws Exception
	 */
	private File configureModel() throws Exception {
		DefaultConfigurationVisitor configurationVisitor = new DefaultConfigurationVisitor(mModel.filename(), out);
		model(out).accept(configurationVisitor);

		out.println(LogMessages.modelConfigurationSuccessful);

		return configurationVisitor.getFile();
	}

	private void validate(KodkodModelValidator modelValidator) {
		enrichModelWithLoadedInvariants(out);
		modelValidator.validate(model(out));
	}
}
