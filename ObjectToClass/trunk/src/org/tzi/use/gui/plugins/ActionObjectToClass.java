package org.tzi.use.gui.plugins;

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.plugins.data.IdResetter;
import org.tzi.use.gui.plugins.objectdiagram.InputObjectDiagramView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.sys.MSystem;

/**
 * This is the ObjectToClass Plugin Action class. It provides the Action which
 * will be performed if the corresponding Plugin Action Delegate in the
 * application is called.
 * 
 * @author Andreas Kaestner
 */
public class ActionObjectToClass implements IPluginActionDelegate {

	final String MODEL_NAME = "ObjectToClassModel";

	/**
	 * Default constructor
	 */
	public ActionObjectToClass() {
	}

	/**
	 * This is the Action Method called from the Action Proxy
	 */
	public void performAction(IPluginAction pluginAction) {
		// Getting Session object from Proxy
		Session session = pluginAction.getSession();
		// Getting MainWindow object from Proxy
		final MainWindow mainWindow = pluginAction.getParent();

		boolean resetEverything = true;
		if (session.hasSystem()) {
			// a system is already loaded
			if (session.system().model().filename().endsWith(MODEL_NAME)) {
				// the loaded system is based on the OTC model
				Object[] options = { "Reload system", "Open window", "Cancel" };
				int option = JOptionPane.showOptionDialog(mainWindow,
						"Do you want to reload the system and remove your progress?\n"
								+ "Or do you want to open a new window with the current objects?",
						"Object to class plugin", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[2]);
				if (option == JOptionPane.YES_OPTION) {
					resetEverything = true;
				} else if (option == JOptionPane.NO_OPTION) {
					resetEverything = false;
				} else {
					return;
				}
			}
		}

		if (resetEverything) {
			IdResetter.resetAllIds();
			compileOTCModel(mainWindow.logWriter(), session);
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				InputObjectDiagramView inodv = new InputObjectDiagramView(mainWindow, session.system());
				ViewFrame f = new ViewFrame("Input object diagram", inodv, "New.gif");
				JComponent c = (JComponent) f.getContentPane();
				c.setLayout(new BorderLayout());
				c.add(inodv, BorderLayout.CENTER);
				// Adding View to the MainWindow
				mainWindow.addNewViewFrame(f);
			}
		});
	}

	private void compileOTCModel(PrintWriter fLogWriter, Session fSession) {
		final String errorMsg = "Could not compile " + MODEL_NAME;
		InputStream iStream = null;
		try {
			iStream = new ByteArrayInputStream(OTCModel.getModel().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fLogWriter.println(errorMsg);
			return;
		}
		MModel model = USECompiler.compileSpecification(iStream, MODEL_NAME, fLogWriter, new ModelFactory());
		if (model == null) {
			fLogWriter.println(errorMsg);
			return;
		}
		fSession.setSystem(new MSystem(model));
		fLogWriter.println(MODEL_NAME + " loaded");
	}
}