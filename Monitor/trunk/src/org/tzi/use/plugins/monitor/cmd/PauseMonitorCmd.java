package org.tzi.use.plugins.monitor.cmd;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.MonitorPlugin;

public class PauseMonitorCmd extends AbstractMonitorCmd {

	@Override
	public void doPerformCommand(IPluginShellCmd pluginCommand) {
		if (!MonitorPlugin.getMonitorPluginInstance().checkMonitoring()) return;
		MonitorPlugin.getMonitorPluginInstance().getMonitor().pause();
	}

}
