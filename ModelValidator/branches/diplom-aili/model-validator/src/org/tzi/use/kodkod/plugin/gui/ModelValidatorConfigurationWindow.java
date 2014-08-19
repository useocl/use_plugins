package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.uml.mm.MModel;


/**
 *  A GUI for the configuration of the model validator
 * 
 * @author Subi Aili
 *
 */
public class ModelValidatorConfigurationWindow extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private File file;
	private HierarchicalINIConfiguration hierarchicalINIConfiguration;
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurationHashtable;
	private PropertiesConfiguration chosenPropertiesConfiguration;
	private Boolean validatable;
	private Boolean somethingChanged;

	private JTabbedPane tabbedPane;
	private JPanel mainPanel;
	private FlowLayout upperLowerPanelLayout;
	private JPanel mainUpperPanel;
	private JPanel mainLowerPanel;
    
	private JButton openFileButton;
	private JButton saveConfigurationButton;
	private JButton validateButton;
	
	private JComboBox<String> sectionSelectionComboBox;
	
	private class ComboBoxActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO: Wenn an der Konfiguration etwas geaendert wurde,
			// sollte vorher gefragt werden, ob man diese Veraenderungen
			// mit dem Wechsel in einen anderen Konfigurationsektor wirklich verwerfen will.
			// Wenn nicht, wird der Konfigurationssektor nicht gewechselt.
			// Pruefung ob etwas geaendert wurde, kann einfacherweise
			// ueber die Boolean-Variable somethingChanged() ermittelt werden.

			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>) e.getSource();
			String selectedSection = (String) cb.getSelectedItem();
			chosenPropertiesConfiguration = propertiesConfigurationHashtable.get(selectedSection);
		}
		
	}
	
	private class BasicTypesTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			String basictype = (String)basicTypesTable.getValueAt(e.getFirstRow(), 0);
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = basicTypesTable.getValueAt(row, col);
			System.out.println("Fuer "+basictype
					+" in Zeile "+row+" und Spalte "+col
					+" ist der Wert "+value+" hinzugefuegt worden!");
			if (basictype.equals("Integer")) {
				switch (col) {
					case 1: chosenPropertiesConfiguration.setProperty("Integer_min", value); break;
					case 2: chosenPropertiesConfiguration.setProperty("Integer_max", value); break;
					case 4: 
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							chosenPropertiesConfiguration.setProperty("Integer", Integer.getInteger(values[0]));
						else
							break;
						for (int i=1; i < values.length; i++) {
							chosenPropertiesConfiguration.addProperty("Integer", Integer.getInteger(values[i]));
						}
						break;
				}
			} else
			if (basictype.equals("Real")) {
				switch (col) {
				case 1: chosenPropertiesConfiguration.setProperty("Real_min", value); break;
				case 2: chosenPropertiesConfiguration.setProperty("Real_max", value); break;
				case 3: chosenPropertiesConfiguration.setProperty("Real_step", value); break;
				case 4: 
					String [] values = preparStringForConfiguration((String) value);
					if (values != null) 
						chosenPropertiesConfiguration.setProperty("Real", Integer.getInteger(values[0]));
					else
						break;
					for (int i=1; i < values.length; i++) {
						chosenPropertiesConfiguration.addProperty("Real", values[i]);
					}
					break;
				}
			} else
			if (basictype.equals("String")) {
				switch (col) {
				case 1: chosenPropertiesConfiguration.setProperty("String_min", value); break;
				case 2: chosenPropertiesConfiguration.setProperty("String_max", value); break;
				case 4: 
					String [] values = preparStringForConfiguration((String) value);
					if (values != null) 
						chosenPropertiesConfiguration.setProperty("String", Integer.getInteger(values[0]));
					else
						break;
					for (int i=1; i < values.length; i++) {
						chosenPropertiesConfiguration.addProperty("String", values[i]);
					}
					break;
				}
			}
			somethingChanged = true;
		}
	}
	
	//TODO: Restliche TableListener machen
	
	ComboBoxActionListener sectionSelectionComboBoxActionListener;
	BasicTypesTableListener basicTypesTableListener;
	
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
			//{"Boolean", 1,	2, 	null,	"true, false"},
			{"Integer",	1,	2, 	null,	"2,3"},
			{"Real", 	3,	4, 	0.5,	"12.2, 17.3"},
			{"String", 	1,	10, null,	"'Ada', 'Bob'"}
	};
	AbstractTableModel basicTypesConfigurationTable = new ConfigurationTableModel(basicTypesTableColumnNames, basicTypesTableData);
	JTable basicTypesTable = new JTable(basicTypesConfigurationTable);

	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		file = new File(model.filename().replaceAll("\\.use", "") + ".properties");
		basicTypesTableListener = new BasicTypesTableListener();
		basicTypesConfigurationTable.addTableModelListener(basicTypesTableListener);
		propertiesConfigurationHashtable = new Hashtable<String, PropertiesConfiguration>();
		validatable = false;

		//TODO: Design durch bessers als das hier verbessern
		getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL); // "blocks all top-level windows from the same Java application except those from its own child hierarchy"
    	this.setResizable(true);
    	this.setSize(800,300);
    	
    	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    	mainPanel = new JPanel();
        upperLowerPanelLayout = new FlowLayout(FlowLayout.LEFT);
        mainUpperPanel = new JPanel(upperLowerPanelLayout);
        mainLowerPanel = new JPanel(upperLowerPanelLayout);
        
        openFileButton = new JButton("Open configuration file");
        saveConfigurationButton = new JButton("Save Configuration");
        validateButton = new JButton("Close and validate");
        
        sectionSelectionComboBox = new JComboBox<String>();
        sectionSelectionComboBoxActionListener = new ComboBoxActionListener();  
        sectionSelectionComboBox.addActionListener(sectionSelectionComboBoxActionListener);
        
        openFileButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		JFileChooser fileChooser = new JFileChooser();
        		if (model.getModelDirectory() != null) {
        			fileChooser = new JFileChooser(model.getModelDirectory().getPath());
        		} else {
        			fileChooser = new JFileChooser();
        		}
        		fileChooser.setFileFilter(new ExtFileFilter("properties", "Property Files"));

        		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
        			file = fileChooser.getSelectedFile();
        			collectConfigurations(file);
        			fillConfigurationInBasicTypesTable(chosenPropertiesConfiguration);
        			collectConfigurations(file); //TODO: Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
        			somethingChanged = false;
        			System.out.println("File loaded.");
        		} else {System.out.println("Loading file failed.");}
        		
        	}
        } );
        
        saveConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		/* Das ist nun obsolete:
        		chosenPropertiesConfiguration = new PropertiesConfiguration();
        		fillBasicTableInChosenConfiguration();
        		fillClassesTableInChosenConfiguration();
        		fillAttributesTableInChosenConfiguration();
        		fillAssociationsTableInChosenConfiguration();
        		*/
        		//TODO: Die Configuration muss in der .properties-Datei an der richtigen Position
        		//gespeichert werden
        	}
        });
        
        //TODO: Bevor die GUI geschlossen wird sollte vorher geprueft werden,
        //ob die veraenderungen bereits uebernommen(Apply-Button gedrueckt) wurden.
        //Warnung ausgeben dass, falls die Konfiguration nicht abgespeichert(Save-Button gedrueckt) wurde,
        //diese in der folgenden Validierung zwar verwendet wird, danach aber verworfen ist.
        validateButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		validatable = true;
        		setVisible(false);
        	}
        } );
        
        tabbedPane.add("Basic Types", createBasicTypesTab());
        tabbedPane.add("Classes and Associations", createClassesAndAssociationsTab());
        mainUpperPanel.add(openFileButton);
        mainUpperPanel.add(sectionSelectionComboBox);
        mainUpperPanel.add(saveConfigurationButton);
        mainLowerPanel.add(validateButton);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(mainUpperPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER); 
        mainPanel.add(mainLowerPanel, BorderLayout.SOUTH);

        collectConfigurations(file);
        fillConfigurationInBasicTypesTable(chosenPropertiesConfiguration);
        collectConfigurations(file); //TODO: Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
        somethingChanged = false;
    	
    	this.setContentPane(mainPanel);
    	this.setLocationRelativeTo(parent);
    	this.pack();
    	this.setVisible(true);
    	
	}
		
	private void collectConfigurations(File file) {
		sectionSelectionComboBox.removeActionListener(sectionSelectionComboBoxActionListener);
		sectionSelectionComboBox.removeAllItems();
		propertiesConfigurationHashtable = new Hashtable<String, PropertiesConfiguration>();
		try {
			hierarchicalINIConfiguration = new HierarchicalINIConfiguration(file);
		} catch (ConfigurationException e) {
			// TODO: Hier muss die Alternative rein, wenn eine .properties-Datei geladen wird, die keine INI-Sektoren hat. 
			// TODO Exception-Message in einem Dialog-Fenster wiedergeben(bei allen anderen auch so machen wie folgend geloest)
			e.printStackTrace();
			return;
		}
		Iterator<String> sectionsIterator = hierarchicalINIConfiguration.getSections().iterator();
		Boolean isFirstConfiguration = true;
		while (sectionsIterator.hasNext()) {
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
			String section = sectionsIterator.next();
			SubnodeConfiguration sectionConfigurations = hierarchicalINIConfiguration.getSection(section);
			Iterator<String> keysIterator = sectionConfigurations.getKeys();
			while (keysIterator.hasNext()) {
				String key = keysIterator.next();
				if (!key.startsWith("--"))
					propertiesConfiguration.addProperty(key, sectionConfigurations.getString(key));
			}
			if (isFirstConfiguration) {
				chosenPropertiesConfiguration = propertiesConfiguration;
				isFirstConfiguration = false;
			}
			propertiesConfigurationHashtable.put(section.toString(), propertiesConfiguration);
			sectionSelectionComboBox.addItem(section.toString());
		}
		sectionSelectionComboBox.addActionListener(sectionSelectionComboBoxActionListener);
	}
	
	public PropertiesConfiguration getChosenPropertiesConfiguration() {
		return chosenPropertiesConfiguration;
	}
	
	public Boolean isValidatable() {
		return validatable;
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
	
	private String [] preparStringForConfiguration(String values) {
		if (values != null) {
			String [] sepValues = values.split("[,]");
			sepValues[0] = "Set{" + sepValues[0].trim();
			for (int i = 1; i < sepValues.length-1; i++) {
				sepValues[i] = sepValues[i].trim();
			}
			sepValues[sepValues.length-1] = sepValues[sepValues.length-1].trim() + "}"; 
			return sepValues;
		} else return null;
	}
	
	private String prepareConfigurationValuesForTable(Object [] array) {
		String string = Arrays.toString(array).trim();
		return string.substring(5, string.length()-2);
	}
	
	private void fillConfigurationInBasicTypesTable(PropertiesConfiguration pc) {
		basicTypesConfigurationTable.setValueAt(pc.getInt("Integer_min"), 0, 1);
		basicTypesConfigurationTable.setValueAt(pc.getInt("Integer_max"), 0, 2);
		if (pc.getStringArray("Integer").length > 0) {
			basicTypesConfigurationTable.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("Integer")), 0, 4);
		} else basicTypesConfigurationTable.setValueAt(null, 0, 4);
		basicTypesConfigurationTable.setValueAt(pc.getInt("Real_min"), 1, 1);
		basicTypesConfigurationTable.setValueAt(pc.getInt("Real_max"), 1, 2);
		basicTypesConfigurationTable.setValueAt(pc.getDouble("Real_step"), 1, 3);
		if (pc.getStringArray("Real").length > 0) {
			basicTypesConfigurationTable.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("Real")), 1, 4);
		} else basicTypesConfigurationTable.setValueAt(null, 1, 4);
		basicTypesConfigurationTable.setValueAt(pc.getInt("String_min"), 2, 1);
		basicTypesConfigurationTable.setValueAt(pc.getInt("String_max"), 2, 2);
		if (pc.getStringArray("String").length > 0) {
			basicTypesConfigurationTable.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("String")), 2, 4);
		} else basicTypesConfigurationTable.setValueAt(null, 2, 4);
	}
	
	private void fillConfigurationInClassesTable(PropertiesConfiguration pc) {
		
	}
	private void fillConfigurationInAttributesTable(PropertiesConfiguration pc) {
		
	}
	private void fillConfigurationInAssociationsTable(PropertiesConfiguration pc) {
		
	}
		
}
