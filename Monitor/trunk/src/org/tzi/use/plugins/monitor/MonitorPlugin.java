package org.tzi.use.plugins.monitor;

import org.tzi.use.runtime.impl.Plugin;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;

public class MonitorPlugin extends Plugin {

	private static String PLUGIN_NAME = "Monitor";
	
	private Monitor monitor = new Monitor();
	
	public static MonitorPlugin getMonitorPluginInstance() {
		return (MonitorPlugin)pluginInstance;
	}
	
	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

	public Monitor getMonitor() {
		return monitor;
	}
	
	public void startMonitor(MSystem system, String host, String port) {
		this.monitor.configure(system, host, port);
		this.monitor.start();
	}

	public boolean checkMonitoring() {
		if (!monitor.isRunning()) {
    		Log.error("No monitoring is running. Please use 'monitor start' to begin monitoring.");
    		return false;
    	}
    	
    	return true;
	}

	public void endMonitor() {
		monitor.end();
	}
}
