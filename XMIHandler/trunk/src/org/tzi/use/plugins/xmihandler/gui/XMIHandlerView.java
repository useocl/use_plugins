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
  private MainWindow parent;

  public enum Mode {
    EXPORT, IMPORT
  }

  public XMIHandlerView(MainWindow theParent, Session theSession, Mode mode) {
    this.session = theSession;
    this.parent = theParent;
    initGUI(mode);
  }

  private void initGUI(final Mode mode) {
    setFileFilter(new FileNameExtensionFilter("Eclipse UML2 (v3.x) XMI (*.uml, *.xmi)", "uml", "xmi"));
    int returnVal = showOpenDialog(parent);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      WaitDialog dlg = new WaitDialog(parent, true);
      dlg.start(new IWorkerRunner() {

        public Object doWork() {
          switch (mode) {
          case EXPORT:
            XMIHandlerPlugin.getXMIHandlerPluginInstance().exportToXMI(
                getSelectedFile(), session.system().model());
            break;
          case IMPORT:
            XMIHandlerPlugin.getXMIHandlerPluginInstance().importFromXMI(
                getSelectedFile(), session);
            break;
          }

          return Boolean.TRUE;
        }

        public void doUpdate() {
        }
      });
      centerWindow(dlg, parent);
      dlg.setVisible(true);

      // This is where a real application would open the file.
    } else {
    }

    this.setVisible(true);
  }

  public void centerWindow(JDialog dlg, JFrame frame) {
    dlg.setLocation(frame.getLocationOnScreen().x + frame.getSize().width
        / 2 - (dlg.getWidth() / 2), frame.getLocationOnScreen().y
        + frame.getSize().height / 2);
  }

}
