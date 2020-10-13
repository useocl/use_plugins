package org.tzi.use.gui.plugins;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

import javax.swing.*;
import java.awt.*;

/**
 * This is the OCLComplexity Plugin Action class. It provides the Action
 * which will be performed if the corresponding Plugin Action Delegate in the
 * application is called.
 * 
 * @author Timo Stüber
 */
public class ActionOCLComplexity implements IPluginActionDelegate {

	/**
	 * Default constructor
	 */
	public ActionOCLComplexity() {
	}

	/**
	 * This is the Action Method called from the Action Proxy
	 */
	public void performAction(IPluginAction pluginAction) {
		if(!pluginAction.getSession().hasSystem()){
			JOptionPane.showMessageDialog(
							pluginAction.getParent(),
							"No model loaded. Please load a model first.",
							"No Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Getting Session object from Proxy
		Session session = pluginAction.getSession();
		// Getting MainWindow object from Proxy
		final MainWindow mainWindow = pluginAction.getParent();
		// Creating OCL Complexity View
		OCLComplexityView ocv = new OCLComplexityView(mainWindow, session.system());
		ViewFrame f = new ViewFrame("OCL Complexity", ocv, "OCLComplexity.png");

		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(ocv, BorderLayout.CENTER);
		// Adding View to the MainWindow
		mainWindow.addNewViewFrame(f);
	}
}
