package org.tzi.use.modelvalidator.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.kodkod.gui.KodkodView;
import org.tzi.use.modelvalidator.gui.ModelValidatorView;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.tzi.use.uml.sys.MSystem;

/**
 * @author Mirco Kuhlmann
 * @author Torsten Humann
 */

public class ActionModelValidator implements IPluginActionDelegate {

	public ActionModelValidator() {
	}

	public void performAction(IPluginAction pluginAction) {
		MSystem system = pluginAction.getSession().system();
		MainWindow mainWindow = pluginAction.getParent();

		ModelValidatorView modelValidatorView = new ModelValidatorView(
				mainWindow, system);
		modelValidatorView.setVisible(true);
		ViewFrame viewFrame = new ViewFrame("Model Validator",
				modelValidatorView, "");
		JComponent c = (JComponent) viewFrame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(new JScrollPane(modelValidatorView), BorderLayout.CENTER);
		mainWindow.addNewViewFrame(viewFrame);
	}
}