package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;

public class ModelValidatorConfigurationWindow extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabbedPane;
	private JPanel mainPanel;
	private FlowLayout upperLowerPanelLayout;
	private JPanel mainUpperPanel;
	private JPanel mainLowerPanel;
    
	private JButton closeButtonA;
	private JButton closeButtonB;
	
	// TODO: Daten aus MModel auslesen, anstatt der folgenden Testdaten.
	String[] associationsTableColumnNames = {"Associations", "Min", "Max"};
	Object[][] associationsTableData = {
			{"belongsTo",	1,	2},
			{"owns", 		3,	4},
			{"cowFarmer", 	2,	6}
	};
	JTable associationsTable = new JTable(associationsTableData, associationsTableColumnNames);
	String[] attributesTableColumnNames = {"Attributes", "Min", "Max", "MinSize", "MaxSize", "Values"};
	Object[][] attributesTableData = {
			{"Person_name",	1,	2,	3,	4,	"adsad"},
			{"Cow_name", 	1,	2,	3,	4,	"ewrewr"},
			{"Cow_weight", 	1,	2,	3,	4,	"zxczxc"}
	};
	JTable attributesTable = new JTable(attributesTableData, attributesTableColumnNames);
	String[] classesTableColumnNames = {"Classes", "Min", "Max", "Values"};
	Object[][] classesTableData = {
			{"Person",	1,	2,	"adsad"},
			{"Cow", 	1,	2,	"ewrewr"},
			{"Device", 	1,	2,	"zxczxc"}
	};
	JTable classesTable = new JTable(classesTableData, classesTableColumnNames);
	String[] basicTypesTableColumnNames = {"Typ", "Min", "Max", "Step", "Values"};
	Object[][] basicTypesTableData = {
			{"Boolean", 1,	2, 	null,	"true, false"},
			{"Integer",	1,	2, 	null,	"2,3"},
			{"Real", 	3,	4, 	0.5,	"12.2, 17.3"},
			{"String", 	1,	10, null,	"'Ada', 'Bob'"}
	};
	JTable basicTypesTable = new JTable(basicTypesTableData, basicTypesTableColumnNames);

	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		//TODO: Design durch bessers als das hier verbessern
		getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL); // "blocks all top-level windows from the same Java application except those from its own child hierarchy"
    	this.setResizable(true);
    	this.setSize(800,300);
    	
    	// Test Anfang Model auslesen
    	Collection<MClass> klassen = model.classes();
    	for (Object klasse : klassen) {
    		System.out.print(klasse
    				+ " ");
    	}
    	System.out.println("");
    	// Test Ende
    	
    	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    	mainPanel = new JPanel();
        upperLowerPanelLayout = new FlowLayout(FlowLayout.LEFT);
        mainUpperPanel = new JPanel(upperLowerPanelLayout);
        mainLowerPanel = new JPanel(upperLowerPanelLayout);
        
        closeButtonA = new JButton("Oberes Ende!");
        closeButtonB = new JButton("Unteres Ende!");
        
        closeButtonA.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		System.out.println("Programm beendet.");
        		System.exit( 0 );
        	}
        } );
        closeButtonB.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		System.out.println("Programm beendet.");
        		System.exit( 0 );
        	}
        } );
        
        tabbedPane.add("Basic Types", createBasicTypesTab());
        tabbedPane.add("Classes and Associations", createClassesAndAssociationsTab());
        mainUpperPanel.add(closeButtonA);
        mainLowerPanel.add(closeButtonB);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(mainUpperPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER); 
        mainPanel.add(mainLowerPanel, BorderLayout.SOUTH);
        
    	
    	this.setContentPane(mainPanel);
    	this.setLocationRelativeTo(parent);
    	this.pack();
    	this.setVisible(true);
	}
	
	private JPanel createBasicTypesTab() {
		//Testdatenmodel
		JScrollPane basicTypesScrollPane = new JScrollPane(basicTypesTable);
		
		JPanel basicTypesPanel = new JPanel(new BorderLayout());
		basicTypesPanel.add(new JLabel("Basic Types"), BorderLayout.NORTH);
		basicTypesPanel.add(basicTypesScrollPane, BorderLayout.CENTER);
		return basicTypesPanel;
	}

	private JSplitPane createClassesAndAssociationsTab() {
		// Anfang Test-Datenmodell
		JScrollPane classesScrollPane = new JScrollPane(classesTable);
        JScrollPane attributesScrollPane = new JScrollPane(attributesTable);
        JScrollPane associationsScrollPane = new JScrollPane(associationsTable);
        // Ende Test-Datenmodell
        JPanel classesPanel = new JPanel(new BorderLayout());
        JPanel attributesPanel = new JPanel(new BorderLayout());
        JPanel associationsPanel = new JPanel(new BorderLayout());
        JSplitPane caaTabLeftSplitPane;
        JSplitPane caaTabRightSplitPane;
        
        classesScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()));
        attributesScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/2));
        associationsScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/2));
        classesPanel.add(new JLabel("Classes"), BorderLayout.NORTH);
        classesPanel.add(classesScrollPane, BorderLayout.CENTER);
        attributesPanel.add(new JLabel("Attributes"), BorderLayout.NORTH);
        attributesPanel.add(attributesScrollPane, BorderLayout.CENTER);
        associationsPanel.add(new JLabel("Associations"), BorderLayout.NORTH);
        associationsPanel.add(associationsScrollPane, BorderLayout.CENTER);
        caaTabRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, attributesPanel, associationsPanel);
        caaTabLeftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, classesPanel, caaTabRightSplitPane);
		return caaTabLeftSplitPane;
	}
	
}
