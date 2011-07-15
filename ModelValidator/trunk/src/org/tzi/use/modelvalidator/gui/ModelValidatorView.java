package org.tzi.use.modelvalidator.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.View;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * @author Mirco Kuhlmann
 * @author Torsten Humann
 */

public class ModelValidatorView extends JPanel implements View {
	private static final long serialVersionUID = 1L;

	private MSystem system;

	public ModelValidatorView(MainWindow mainWindow, MSystem system) {
		super(new BorderLayout());
		this.system = system;
		system.addChangeListener(this);
		
		JTabbedPane modelValidatorTabs = new JTabbedPane();
		this.add(modelValidatorTabs, BorderLayout.CENTER);
		
		JPanel classBoundsPanel = new JPanel(new BorderLayout());
		modelValidatorTabs.add("Class Bounds", classBoundsPanel);

		JTable classBoundsTable = new JTable(new ClassBoundsTableModel(system));
		JScrollPane classBoundsTablePane = new JScrollPane(classBoundsTable);
		
		classBoundsPanel.add(classBoundsTablePane, BorderLayout.CENTER);
	}

	public void stateChanged(StateChangeEvent e) {
		// TODO Auto-generated method stub

	}

	public void detachModel() {
		system.removeChangeListener(this);
	}

}