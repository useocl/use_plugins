package org.tzi.use.kodkod.plugin;

import javax.swing.JOptionPane;

import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/**
 * Action-Class to extend the toolbar with a button to call a GUI to configurate 
 * the model validator with a configuration file.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodValidatePropertyAction extends KodkodValidateCmd implements IPluginActionDelegate {

	@Override
	public void performAction(IPluginAction pluginAction) {
		if(!pluginAction.getSession().hasSystem()){
			JOptionPane.showMessageDialog(pluginAction.getParent(),
					"No model present.", "No Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		initialize(pluginAction.getSession(), pluginAction.getParent());
		
		// TODO: Validation should also run in one thread with the GUI of the MV-Configuration
		//for using the MV-GUI and USE-GUI and the same time.
		getConfigurationOverGUIAndValidate(pluginAction);
		
	}

}
