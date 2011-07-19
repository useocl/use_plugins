package org.tzi.use.plugins.monitor;

import org.tzi.use.main.Session;
import org.tzi.use.runtime.impl.Plugin;
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
	
	public void startMonitor(Session session, String host, String port, boolean suspend) {
		this.monitor.configure(session, host, port);
		this.monitor.start(suspend);
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
