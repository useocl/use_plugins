package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.InvariantIndepChecker;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.PropertyConfigurationVisitor;
import org.tzi.use.config.Options;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;

/**
 * Cmd-Class for the invariant independence check.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class InvariantIndepCmd extends ConfigurablePlugin implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if(!pluginCommand.getSession().hasSystem()){
			LOG.error("No model loaded.");
			return;
		}
		
		initialize(pluginCommand.getSession());
		enrichModel();

		InvariantIndepChecker indepChecker = new InvariantIndepChecker(session);

		String[] arguments = pluginCommand.getCmdArgumentList();

		// kodkod -invIndep <propertyFile> (all|<className>::<invName>)
		if(arguments.length != 2){
			LOG.error("Invalid parameters.");
			LOG.error("Syntax of command is: " + pluginCommand.getCmd() + " <propertyFile> (all|<className>::<invName>)");
			return;
		}
		
		String filenameToOpen = Shell.getInstance().getFilenameToOpen(arguments[0], false);
		filenameToOpen = Options.getFilenameToOpen(filenameToOpen);
		try {
			PropertyConfigurationVisitor configurationVisitor = configureModel(new File(filenameToOpen));
			configurationVisitor.printWarnings();
		} catch (ConfigurationException e) {
			LOG.error(LogMessages.propertiesConfigurationReadError + ". " + (e.getMessage() != null ? e.getMessage() : ""));
			return;
		}
		
		if(arguments[1].equalsIgnoreCase("all")){
			indepChecker.validate(model());
		}
		else {
			String[] split = arguments[1].split("-|:{2}");
			if (split.length != 2) {
				LOG.error(LogMessages.invIndepSyntaxError(arguments[1]));
			} else {
				indepChecker.validate(model(), split[0], split[1]);
			}
		}
	}
		
}
