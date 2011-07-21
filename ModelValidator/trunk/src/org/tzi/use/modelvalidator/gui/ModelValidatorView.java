package org.tzi.use.modelvalidator.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import kodkod.engine.Solution;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.View;
import org.tzi.use.modelvalidator.configuration.ClassConfiguration;
import org.tzi.use.modelvalidator.main.Main;
import org.tzi.use.modelvalidator.main.ModelValidator;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * @author Mirco Kuhlmann
 */

public class ModelValidatorView extends JPanel implements View {
	private static final long serialVersionUID = 1L;

	private MSystem system;

	ClassBoundsTableModel classBoundsTableModel;

	public ModelValidatorView(MainWindow mainWindow, MSystem system) {
		super(new BorderLayout());
		this.system = system;
		system.addChangeListener(this);

		JPanel searchBoundsPanel = new JPanel(new BorderLayout());
		this.add(searchBoundsPanel, BorderLayout.CENTER);

		JTabbedPane modelValidatorTabs = new JTabbedPane();
		searchBoundsPanel.add(modelValidatorTabs, BorderLayout.CENTER);

		JPanel classBoundsPanel = new JPanel(new BorderLayout());
		modelValidatorTabs.add("Class Bounds", classBoundsPanel);

		classBoundsTableModel = new ClassBoundsTableModel(system);
		JTable classBoundsTable = new JTable(classBoundsTableModel);
		classBoundsTableModel.addTableModelListener(classBoundsTable);
		classBoundsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		classBoundsTable.setRowSelectionAllowed(false);
		classBoundsTable.setColumnSelectionAllowed(false);
		classBoundsPanel.add(new JScrollPane(classBoundsTable),
				BorderLayout.CENTER);

		JPanel actionButtonPanel = new JPanel();
		this.add(actionButtonPanel, BorderLayout.SOUTH);

		JButton startSearchButton = new JButton("Start Search", new ImageIcon(
				Main.getInstance().getResource("resources/startSearch.png")));
		actionButtonPanel.add(startSearchButton);

		startSearchButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startKodkod();
			}
		});

	}

	private void startKodkod() {
		List<ClassConfiguration> classConfigurations = new ArrayList<ClassConfiguration>();

		boolean universeNotEmpty = false;
		for (ClassBoundsTableModel.Row row : classBoundsTableModel.getRows()) {
			universeNotEmpty = universeNotEmpty
					|| row.getMaximumNumberOfObjects() > 0;
		}

		if (universeNotEmpty) {

			for (ClassBoundsTableModel.Row row : classBoundsTableModel
					.getRows()) {
				List<String> concreteObjectsMandatory = new ArrayList<String>();
				if (row.getConcreteObjectsMandatoryFix() != null) {
					List<String> concreteObjectsMandatoryFix = Arrays
							.asList(row.getConcreteObjectsMandatoryFix()
									.replaceAll(" ", "").split(","));
					concreteObjectsMandatory
							.addAll(concreteObjectsMandatoryFix);
				}
				if (row.getConcreteObjectsMandatoryAdditional() != null) {
					List<String> concreteObjectsMandatoryAdditional = Arrays
							.asList(row.getConcreteObjectsMandatoryAdditional()
									.replaceAll(" ", "").split(","));
					concreteObjectsMandatory
							.addAll(concreteObjectsMandatoryAdditional);
				}

				List<String> concreteObjectsOptional = new ArrayList<String>();
				if (row.getConcreteObjectsOptional() != null) {
					concreteObjectsOptional = new ArrayList<String>(
							Arrays.asList(row.getConcreteObjectsOptional()
									.replaceAll(" ", "").split(",")));
				}

				classConfigurations.add(new ClassConfiguration(row.getCls(),
						concreteObjectsMandatory, concreteObjectsOptional, row
								.getMinimumNumberOfObjects(), row
								.getMaximumNumberOfObjects()));
			}

			ModelValidator modelValidator = new ModelValidator(
					classConfigurations);
			modelValidator.translateUML();
			Solution solution = modelValidator.startSearch();
			
			System.out.println(solution);

		} else {
			JOptionPane
					.showMessageDialog(
							this,
							"Die maximale Anzahl an Objekten muss für mindestens eine Klasse größer als 0 sein.");
		}
	}

	public void stateChanged(StateChangeEvent e) {
	}

	public void detachModel() {
		system.removeChangeListener(this);
	}

}