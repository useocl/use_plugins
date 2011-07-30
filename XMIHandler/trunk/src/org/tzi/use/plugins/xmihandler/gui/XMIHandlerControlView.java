package org.tzi.use.plugins.xmihandler.gui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;

import sun.awt.VerticalBagLayout;

@SuppressWarnings("serial")
public class XMIHandlerControlView extends JDialog implements StateChangeListener, ChangeListener{

	private Session session;
	
	public XMIHandlerControlView(MainWindow parent, Session session) {
		super(parent, "XMIHandler Control");
		this.session = session;
		session.addChangeListener(this);
		initGUI();
	}
	
	private void initGUI() {
		JPanel backPanel = new JPanel(new VerticalBagLayout());
		this.getContentPane().add(backPanel);
		this.pack();
    this.setSize(350, 250);		
		this.setMinimumSize(this.getSize());
	}

	@Override
	public void stateChanged(StateChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
	}
}
