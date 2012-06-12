package org.tzi.use.plugins.monitor.cmd;

import java.util.logging.Level;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.LogListener;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.Log;

/**
 * Base class for all monitor commands.
 * Registers itself for the monitor output before the concrete command
 * is executed by {@link #doPerformCommand(IPluginShellCmd)}. 
 * @author Lars Hamann
 */
public abstract class AbstractMonitorCmd implements IPluginShellCmdDelegate, LogListener {

	/* (non-Javadoc)
	 * @see org.tzi.use.runtime.shell.IPluginShellCmdDelegate#performCommand(org.tzi.use.main.shell.runtime.IPluginShellCmd)
	 */
	@Override
	public final void performCommand(IPluginShellCmd pluginCommand) {
		MonitorPlugin.getInstance().getMonitor().addLogListener(this);
		doPerformCommand(pluginCommand);
		MonitorPlugin.getInstance().getMonitor().removeLogListener(this);
	}

	/**
	 * Subclasses override this to perfom the concrete command.
	 * @param pluginCommand
	 */
	protected abstract void doPerformCommand(IPluginShellCmd pluginCommand);
	
	@Override
	public void newLogMessage(Object source, Level level, String message) {
		if (Log.isDebug() || level.intValue() >= Level.INFO.intValue())
			Log.println(message);
	}
}
