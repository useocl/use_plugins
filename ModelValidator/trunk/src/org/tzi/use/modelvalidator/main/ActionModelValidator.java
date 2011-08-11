package org.tzi.use.modelvalidator.main;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.modelvalidator.gui.ModelValidatorView;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.tzi.use.uml.sys.MSystem;

public class ActionModelValidator implements IPluginActionDelegate {

	public ActionModelValidator() {
	}

	public void performAction(IPluginAction pluginAction) {
		MSystem system = pluginAction.getSession().system();
		MainWindow mainWindow = pluginAction.getParent();

		ModelValidatorView modelValidatorView = 
			new ModelValidatorView(mainWindow, system);
		modelValidatorView.setVisible(true);
		ViewFrame viewFrame = 
			new ViewFrame("Model Validator", modelValidatorView, "");
		
		JComponent contentPane = (JComponent) viewFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(modelValidatorView, BorderLayout.CENTER);
		
		mainWindow.addNewViewFrame(viewFrame);
	}
}