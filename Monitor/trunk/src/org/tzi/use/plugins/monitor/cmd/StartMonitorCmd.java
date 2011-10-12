package org.tzi.use.plugins.monitor.cmd;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.util.Log;

public class StartMonitorCmd extends AbstractMonitorCmd {

	@Override
	public void doPerformCommand(IPluginShellCmd pluginCommand) {
		if (MonitorPlugin.getMonitorPluginInstance().getMonitor().isRunning()) {
    		Log.error("Already monitioring an application. Please stop before starting a new monitor.");
    		return;
    	}
    	
    	String[] args = pluginCommand.getCmdArguments().split(" ");
    	String host = "";
    	String port = "";

    	if (args.length == 1) {
    		String[] hostAndPort = args[0].split(":");
    		if (hostAndPort.length == 1) {
        		port = hostAndPort[0];
        	} else {
        		host = hostAndPort[0];
        		port = hostAndPort[1];
        	}
    	} else if (args.length == 0) {
    		Log.println("Using default value for remote debugger: localhost:6000");
    	} else {
    		Log.println("Wrong number of arguments. Usage: start monitor [hostname:]port [suspend]");
    		return;
    	}
    	
		MonitorPlugin.getMonitorPluginInstance().startMonitor(pluginCommand.getSession(), host, port, false);
	}

	

}
