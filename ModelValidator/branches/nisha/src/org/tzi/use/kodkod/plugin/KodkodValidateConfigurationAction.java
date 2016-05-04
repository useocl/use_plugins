package org.tzi.use.kodkod.plugin;

import javax.swing.JOptionPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.plugin.gui.ModelValidatorConfigurationWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/**
 * Action-Class to extend the toolbar with a button to call a GUI to configurate 
 * the model validator with a configuration file.
 * 
 * @author Subi Aili
 * @author Frank Hilken
 */
public class KodkodValidateConfigurationAction extends KodkodValidateCmd implements IPluginActionDelegate {
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		if(!pluginAction.getSession().hasSystem()){
			JOptionPane.showMessageDialog(pluginAction.getParent(),
					"No model present.", "No Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		initialize(pluginAction.getSession(), pluginAction.getParent());
		
		ModelValidatorConfigurationWindow modelValidatorConfigurationWindow = 
				new ModelValidatorConfigurationWindow(MainWindow.instance(), model(), mModel.filename());
		if (modelValidatorConfigurationWindow.getChosenConfiguration() != null) {
			if (modelValidatorConfigurationWindow.isReadyToValidate()) {
				extractConfigureAndValidate(modelValidatorConfigurationWindow.getChosenConfiguration());
			}
		} else {
			JOptionPane.showMessageDialog(MainWindow.instance(), "No Configuration loaded!");
		}
	}

}
