package org.tzi.use.plugins.xmihandler.gui;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.xmihandler.XMIHandlerPlugin;

@SuppressWarnings("serial")
public class XMIHandlerView extends JFileChooser {

  private Session session;
  private MainWindow mainWindow;

  public enum Mode {
    EXPORT, IMPORT
  }

  public XMIHandlerView(MainWindow theParent, Session theSession, Mode mode) {
    this.session = theSession;
    this.mainWindow = theParent;
    initGUI(mode);
  }

  private void initGUI(final Mode mode) {
    setFileFilter(new FileNameExtensionFilter("Eclipse UML2 (v3.x) XMI (*.uml, *.xmi)", "uml", "xmi"));
    int returnVal = -1;
    if (mode == Mode.EXPORT) {
      setDialogType(JFileChooser.SAVE_DIALOG);
      returnVal = showSaveDialog(mainWindow);      
    } else {
      setDialogType(JFileChooser.OPEN_DIALOG);
      returnVal = showOpenDialog(mainWindow);      
    }
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      WaitDialog dlg = new WaitDialog(mainWindow, true);
      dlg.start(new IWorkerRunner() {

        public Object doWork() {
          switch (mode) {
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

        public void doUpdate() {
        }
      });
      centerWindow(dlg, mainWindow);
      dlg.setVisible(true);

    } else { // do nothing
    }

    this.setVisible(true);
  }

  public void centerWindow(JDialog dlg, JFrame frame) {
    dlg.setLocation(frame.getLocationOnScreen().x + frame.getSize().width
        / 2 - (dlg.getWidth() / 2), frame.getLocationOnScreen().y
        + frame.getSize().height / 2);
  }

}
