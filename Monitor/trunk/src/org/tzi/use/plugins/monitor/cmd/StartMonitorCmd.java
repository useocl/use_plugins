package org.tzi.use.plugins.monitor.cmd;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;

public class StartMonitorCmd extends AbstractMonitorCmd {

	@Override
	public void doPerformCommand(IPluginShellCmd pluginCommand) {
		if (MonitorPlugin.getInstance().getMonitor().isRunning()) {
    		Log.error("Already monitioring an application. Please stop before starting a new monitor.");
    		return;
    	}
    	
    	String[] args = pluginCommand.getCmdArguments().split(" ");

    	if (args.length == 0) {
    		Log.println("Using default value for JVM remote debugger: localhost:6000");
    	} 
    	
    	String adpaterName = args[0];
		VMAdapter adapter = MonitorPlugin.getInstance().getAdapterRegistry().getAdapterByName(adpaterName);
    	
    	if (adapter == null) {
    		Log.print("Invalid adapter name " + StringUtil.inQuotes(adpaterName) + " specified.");
    		return;
    	}
    		
    	for (int i = 1; i < args.length;++i) {
    		
    	}
    	
		try {
			MonitorPlugin.getInstance().startMonitor(pluginCommand.getSession(), adapter, false);
		} catch (InvalidAdapterConfiguration e) {
			Log.println("Invalid adapter configuration: " + e.getMessage());
		}
	}

	

}
