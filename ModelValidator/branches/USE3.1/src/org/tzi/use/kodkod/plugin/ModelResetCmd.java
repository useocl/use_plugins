package org.tzi.use.kodkod.plugin;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;

/**
 * Cmd-Class to reset the model to the transformation state.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ModelResetCmd extends AbstractPlugin implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		initialize(pluginCommand.getSession());
		model().reset();
	}

}
