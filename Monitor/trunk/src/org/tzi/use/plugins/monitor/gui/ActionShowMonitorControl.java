package org.tzi.use.plugins.monitor.gui;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/**
 * This action initializes and opens a new window to
 * graphically control the monitoring process
 * 
 * @author Lars Hamann
 * 
 */
public class ActionShowMonitorControl implements IPluginActionDelegate {

	/**
	 * Default constructor
	 */
	public ActionShowMonitorControl() {	}
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		// Getting MainWindow object from Proxy
		MainWindow fMainWindow = pluginAction.getParent();
		
		MonitorControlView view = new MonitorControlView(fMainWindow, pluginAction.getSession());
		view.setVisible(true);
	}

}
