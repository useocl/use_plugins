package org.tzi.use.kodkod.plugin;

import org.tzi.kodkod.InvariantIndepChecker;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.Log;

/**
 * Cmd-Class for the invariant independence check.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class InvariantIndepCmd extends AbstractPlugin implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if(!pluginCommand.getSession().hasSystem()){
			LOG.error("No model loaded.");
			return;
		}
		
		initialize(pluginCommand.getSession());
		enrichModel();

		InvariantIndepChecker indepChecker = new InvariantIndepChecker();

		String argument = pluginCommand.getCmdArguments();

		if (argument != null && argument.length() > 1) {
			argument = argument.substring(1).trim();
			String[] split = argument.split("-");

			if (split.length != 2) {
				Log.error(LogMessages.invIndepSyntaxError(argument));
			} else {
				indepChecker.validate(model(), split[0], split[1]);
			}
		} else {
			indepChecker.validate(model());
		}
	}
}
