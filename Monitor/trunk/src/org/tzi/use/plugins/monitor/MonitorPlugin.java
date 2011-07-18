package org.tzi.use.plugins.monitor;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;

public class MonitorPlugin implements IPlugin {

	private static String PLUGIN_NAME = "Monitor";
	
	private static MonitorPlugin pluginSingleton;
	
	private Monitor monitor = null;
	
	public static MonitorPlugin getInstance() {
		return pluginSingleton;
	}
	
	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

	@Override
	public void run(IPluginRuntime pluginRuntime) throws Exception {
		pluginSingleton = this;
	}

	public Monitor getMonitor() {
		return monitor;
	}
	
	public void startMonitor(MSystem system, String host, String port) {
		this.monitor = new Monitor(system, host, port);
	}

	public boolean checkMonitoring() {
		if (monitor == null || !monitor.isRunning()) {
    		Log.error("No monitoring is running. Please use 'monitor start' to begin monitoring.");
    		return false;
    	}
    	
    	return true;
	}

	public void endMonitor() {
		monitor.end();
		monitor = null;
	}
}
