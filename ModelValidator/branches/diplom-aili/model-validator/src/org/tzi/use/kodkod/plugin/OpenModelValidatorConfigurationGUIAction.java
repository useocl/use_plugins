package org.tzi.use.kodkod.plugin;

import javax.swing.JOptionPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.plugin.gui.ModelValidatorConfigurationWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

public class OpenModelValidatorConfigurationGUIAction implements IPluginActionDelegate{

	@Override
	public void performAction(IPluginAction pluginAction) {
		if(!pluginAction.getSession().hasSystem()){
			JOptionPane.showMessageDialog(
							pluginAction.getParent(),
							"No model loaded. The transformation is based on the currently loaded model. Please load a model.",
							"No Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		new ModelValidatorConfigurationWindow(MainWindow.instance(), pluginAction.getSession().system().model());
	}

}
