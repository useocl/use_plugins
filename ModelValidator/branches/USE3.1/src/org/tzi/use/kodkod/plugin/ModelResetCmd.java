package org.tzi.use.kodkod.plugin;

import java.io.PrintWriter;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.Log;

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
		model(new PrintWriter(Log.out())).reset();
	}

}
