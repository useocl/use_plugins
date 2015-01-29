package org.tzi.use.kodkod.plugin;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.UseDefaultConfigKodkodModelValidator;
import org.tzi.use.kodkod.UseKodkodModelValidator;
import org.tzi.use.kodkod.plugin.gui.ModelValidatorConfigurationWindow;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.uml.mm.MClassInvariant;

/**
 * Cmd-Class for a simple model validation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodValidateCmd extends ConfigurablePlugin implements IPluginShellCmdDelegate {

	private PropertyConfigurationVisitor configurationVisitor;
	private Boolean shouldValidate;

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if(!pluginCommand.getSession().hasSystem()){
			LOG.error("No model loaded.");
			return;
		}
		
		initialize(pluginCommand.getSession());

		//String arguments = pluginCommand.getCmdArguments();
		//TODO: use pluginCommand.getCmdArgumentsList() to use the second argument as 
		//sector name of the properties file to be used
		//Falls, kein zweites Argument neben File geschrieben wurde, einen Hinweis anzeigen, der sagt,
		//dass die Datei mehrere Sektoren enthaelt und man nun den ersten Sektor zur Validierungskonfiguration
		//nutzt
		
		String [] arguments = pluginCommand.getCmdArgumentList();
		
		if (arguments.length >= 1) {
			if (arguments.length < 2) {
				handleArguments(arguments[0]);
			} else {
				handleArguments(arguments[0], arguments[1]);
			}
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
	 * @param fileName
	 */
	protected void handleArguments(String fileName) {
		String filepath = Shell.getInstance().getFilenameToOpen(fileName.trim(), false);
		filepath = Options.getFilenameToOpen(filepath);
		File file = new File(filepath);

		if (file.exists() && file.canRead() && !file.isDirectory()) {
			LOG.warn("Using first configuration sector of the file.");
			extractConfigureAndValidate(file);
		} else {
			LOG.error(LogMessages.fileCmdError(file));
		}
	}
	
	/**
	 * Handling of the cmd call with a path to a configuration file and
	 * the sector selected from it.
	 * 
	 * @param arguments
	 */
	protected void handleArguments(String fileName, String section) {
		String filepath = Shell.getInstance().getFilenameToOpen(fileName.trim(), false);
		filepath = Options.getFilenameToOpen(filepath);
		File file = new File(filepath);

		if (file.exists() && file.canRead() && !file.isDirectory()) {
			extractConfigureAndValidate(file, section);
		} else {
			LOG.error(LogMessages.fileCmdError(file));
		}
	}
	
	/**
	 * Configures the model from the first configuration section of the configuration file,
	 * extracts an object diagram and calls the model validator.
	 * 
	 * @param file
	 */
	protected final void extractConfigureAndValidate(File file) {
		try {
			PropertyConfigurationVisitor configurationVisitor = configureModel(file);
			enrichModel();
			validate(createValidator());
			configurationVisitor.printWarnings();
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
		}
	}

	/**
	 * Configures the model from the given configuration section of the configuration file,
	 * extracts an object diagram and calls the model validator.
	 * 
	 * @param file
	 */
	protected final void extractConfigureAndValidate(File file, String section) {
		try {
			PropertyConfigurationVisitor configurationVisitor = configureModel(file, section);
			enrichModel();
			validate(createValidator());
			configurationVisitor.printWarnings();
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
		}
	}
	
	/**
	 * Opens the GUI for configuration of the Model Validator
	 */
	protected final void getConfigurationOverGUIAndValidate(IPluginAction pluginAction) {
		try {
			configureModel(pluginAction);
			if (shouldValidate) {
				enrichModel();
				validate(createValidator());
				configurationVisitor.printWarnings();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(pluginAction.getParent(), new JLabel("Error reading default properties file([Modelname].properties)! Please correct the file or delete it!"));
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
	
	private void configureModel(IPluginAction pluginAction) throws Exception {
		model().reset();
        ModelValidatorConfigurationWindow modelValidatorConfigurationWindow = 
        		new ModelValidatorConfigurationWindow(MainWindow.instance(), mModel);
        if (modelValidatorConfigurationWindow.getChosenPropertiesConfiguration() != null) {
        	if (modelValidatorConfigurationWindow.isValidatable()) {
	        	configurationVisitor = new PropertyConfigurationVisitor(modelValidatorConfigurationWindow.getChosenPropertiesConfiguration());
	        	modelValidatorConfigurationWindow.dispose();
	        	model().accept(configurationVisitor);
	        	shouldValidate = true;
	        	if (configurationVisitor.containErrors()) {
	        		throw new ConfigurationException();
	        	}
	        	LOG.info(LogMessages.modelConfigurationSuccessful);
        	} else { 
        		shouldValidate = false;
        		System.out.println("Validation aborted.");
        	}
        } else {
        	JOptionPane.showMessageDialog(MainWindow.instance(), new JLabel("No Configuration loaded!"));
        }
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
