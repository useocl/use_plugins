package org.tzi.use.plugins.xmihandler.gui;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/**
 * This action opens a dialog to select file for export,
 * which later performs export if file is selected
 * 
 * @author Emil Huseynli
 * 
 */
public class ActionExportXMI implements IPluginActionDelegate {

	/**
	 * Default constructor
	 */
	public ActionExportXMI() {	}
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		// Getting MainWindow object from Proxy
		MainWindow fMainWindow = pluginAction.getParent();
		
		XMIHandlerView view = new XMIHandlerView(fMainWindow, pluginAction.getSession(), XMIHandlerView.ViewMode.EXPORT);
		view.setVisible(true);
	}

}
