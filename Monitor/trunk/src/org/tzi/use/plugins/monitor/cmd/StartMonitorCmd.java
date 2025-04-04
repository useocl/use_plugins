package org.tzi.use.plugins.monitor.cmd;

import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;

public class StartMonitorCmd extends AbstractMonitorCmd {

	@Override
	public void doPerformCommand(IPluginShellCmd pluginCommand) {
		if (MonitorPlugin.getInstance().getMonitor().isRunning()) {
    		Log.error("Already monitoring an application. Please stop before starting a new monitor.");
    		return;
    	}
    	
    	String[] args = pluginCommand.getCmdArgumentList();
		String adapterName;
    	if (args.length == 0) {
			adapterName = JVMAdapter.JVM_ADAPTER_NAME;
    		Log.println("Using default value for JVM remote debugger: localhost:6000");
    	} else {
			adapterName = args[0];
		}
    	
		VMAdapter adapter = MonitorPlugin.getInstance().getAdapterRegistry().getAdapterByName(adapterName);
    	if (adapter == null) {
    		Log.println("Invalid adapter name " + StringUtil.inQuotes(adapterName) + " specified.");
    		return;
    	}
    	
		try {
			MonitorPlugin.getInstance().startMonitor(pluginCommand.getSession(), adapter, false);
		} catch (InvalidAdapterConfiguration e) {
			Log.println("Invalid adapter configuration: " + e.getMessage());
		}
	}

}
