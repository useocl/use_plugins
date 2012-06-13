package org.tzi.use.plugins.monitor;

import java.util.Map;

import org.tzi.use.main.Session;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.runtime.impl.Plugin;
import org.tzi.use.util.Log;

public class MonitorPlugin extends Plugin {

	private static String PLUGIN_NAME = "Monitor";
	
	private static MonitorPlugin pluginInstance;
	
	private Monitor monitor = new Monitor();
	
	private VMAdapter adapter = new JVMAdapter();
	
	public static MonitorPlugin getInstance() {
		return pluginInstance;
	}
	
	/**
	 * Set the static instance of this plugin
	 */
	public MonitorPlugin() {
		super();
		pluginInstance = this;
	}
	
	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

	public Monitor getMonitor() {
		return monitor;
	}
	
	public void setAdapter(VMAdapter a) {
		
	}
	
	public void startMonitor(Session session, Map<String, String> args,
			boolean suspend) throws InvalidAdapterConfiguration {
		this.monitor.configure(session, adapter, args);
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
