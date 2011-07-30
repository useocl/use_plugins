package org.tzi.use.plugins.xmihandler.gui;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/**
 * This action initializes and opens a new window to
 * graphically control the monitoring process
 * 
 * @author Emil Huseynli
 * 
 */
public class ActionShowXMIHandlerControl implements IPluginActionDelegate {

	/**
	 * Default constructor
	 */
	public ActionShowXMIHandlerControl() {	}
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		// Getting MainWindow object from Proxy
		MainWindow fMainWindow = pluginAction.getParent();
		
		XMIHandlerControlView view = new XMIHandlerControlView(fMainWindow, pluginAction.getSession());
		view.setVisible(true);
	}

}
