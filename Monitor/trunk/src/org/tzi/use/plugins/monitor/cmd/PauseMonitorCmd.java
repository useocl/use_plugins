package org.tzi.use.plugins.monitor.cmd;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;

public class PauseMonitorCmd implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		if (!MonitorPlugin.getInstance().checkMonitoring()) return;
		MonitorPlugin.getInstance().getMonitor().pause();
	}

}
