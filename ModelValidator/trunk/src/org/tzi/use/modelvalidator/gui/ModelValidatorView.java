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

import kodkod.engine.Solution;
import kodkod.engine.Solution.Outcome;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.View;
import org.tzi.use.modelvalidator.configuration.ClassConfiguration;
import org.tzi.use.modelvalidator.main.Main;
import org.tzi.use.modelvalidator.main.ModelValidator;
import org.tzi.use.modelvalidator.solution.ModelValidatorSolution;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;

/**
 * @author Mirco Kuhlmann
 */

public class ModelValidatorView extends JPanel implements View {
	private static final long serialVersionUID = 1L;

	private MSystem system;

	ClassBoundsTableModel classBoundsTableModel;
	AttributeBoundsTableModel attributeBoundsTableModel;
	DomainTableModel domainTableModel;

	public ModelValidatorView(MainWindow mainWindow, MSystem system) {
		super(new BorderLayout());
		this.system = system;
		system.addChangeListener(this);

		// main panel
		JPanel searchBoundsPanel = new JPanel(new BorderLayout());
		this.add(searchBoundsPanel, BorderLayout.CENTER);

		// tabbed pane 
		JTabbedPane modelValidatorTabs = new JTabbedPane();
		searchBoundsPanel.add(modelValidatorTabs, BorderLayout.CENTER);

		// class panel
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
		
		// attribute panel
		JPanel attributeBoundsPanel = new JPanel(new BorderLayout());
		modelValidatorTabs.add("Attribute Bounds", attributeBoundsPanel);

		attributeBoundsTableModel = new AttributeBoundsTableModel(system);
		JTable attributeBoundsTable = new JTable(attributeBoundsTableModel);
		attributeBoundsTableModel.addTableModelListener(attributeBoundsTable);
		attributeBoundsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		attributeBoundsTable.setRowSelectionAllowed(false);
		attributeBoundsTable.setColumnSelectionAllowed(false);
		attributeBoundsPanel.add(new JScrollPane(attributeBoundsTable),
				BorderLayout.CENTER);
		
		// domain panel
		JPanel domainPanel = new JPanel(new BorderLayout());
		modelValidatorTabs.add("Attribute Domains", domainPanel);
		
		domainTableModel = new DomainTableModel(system);
		JTable domainTable = new JTable(domainTableModel);
		domainTableModel.addTableModelListener(domainTable);
		domainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		domainTable.setRowSelectionAllowed(false);
		domainTable.setColumnSelectionAllowed(false);
		domainPanel.add(new JScrollPane(domainTable),
				BorderLayout.CENTER);
		
		// search panel
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
			if (solution.outcome().equals(Outcome.TRIVIALLY_UNSATISFIABLE)
					|| solution.outcome().equals(Outcome.UNSATISFIABLE)) {
				JOptionPane.showMessageDialog(this,
						"Kein valider Snapshot gefunden.");
			} else {
				ModelValidatorSolution modelValidatorSolution = new ModelValidatorSolution(
						solution, classConfigurations);
				modelValidatorSolution.setSnapshot(system);
			}
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