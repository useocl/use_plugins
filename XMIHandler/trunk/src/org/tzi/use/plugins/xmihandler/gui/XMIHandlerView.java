package org.tzi.use.plugins.xmihandler.gui;

import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.xmihandler.XMIHandlerPlugin;
import org.tzi.use.plugins.xmihandler.utils.IWorkerRunner;
import org.tzi.use.plugins.xmihandler.utils.Utils;

@SuppressWarnings("serial")
public class XMIHandlerView extends JFileChooser {

	private Session session;
	private MainWindow mainWindow;

	public enum ViewMode {
		EXPORT, IMPORT
	}

	public XMIHandlerView(MainWindow theParent, Session theSession, ViewMode viewMode) {
		this.session = theSession;
		this.mainWindow = theParent;
		initGUI(viewMode);
	}

	@Override
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	private void initGUI(final ViewMode viewMode) {
		setFileFilter(new FileNameExtensionFilter("Eclipse UML2 (v3.x) XMI (*.uml, *.xmi)", "uml", "xmi"));
		int returnVal = -1;
		setCurrentDirectory(Utils.getCurrentDirectory());
		if (viewMode == ViewMode.EXPORT) {
			setDialogTitle("Export to XMI");
			returnVal = showDialog(mainWindow, "Export");
		} else {
			setDialogTitle("Import from XMI");
			returnVal = showDialog(mainWindow, "Import");
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Utils.setCurrentDirectory(getSelectedFile());
			WaitDialog dlg = new WaitDialog(mainWindow, true);
			dlg.start(new IWorkerRunner() {

				@Override
				public Object doWork() {
					switch (viewMode) {
					case EXPORT:
						XMIHandlerPlugin.getXMIHandlerPluginInstance().exportToXMI(
								getSelectedFile(), session, mainWindow.logWriter());
						break;
					case IMPORT:
						XMIHandlerPlugin.getXMIHandlerPluginInstance().importFromXMI(
								getSelectedFile(), session, mainWindow.logWriter());
						break;
					}
					return Boolean.TRUE;
				}

				@Override
				public void doUpdate() {
				}
			});
			centerWindow(dlg, mainWindow);
			dlg.setVisible(true);

		}
	}

	public void centerWindow(JDialog dlg, JFrame frame) {
		dlg.setLocation(frame.getLocationOnScreen().x + frame.getSize().width
				/ 2 - (dlg.getWidth() / 2), frame.getLocationOnScreen().y
				+ frame.getSize().height / 2);
	}

}
