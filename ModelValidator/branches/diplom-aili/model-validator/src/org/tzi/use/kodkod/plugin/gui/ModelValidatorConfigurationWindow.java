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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
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
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurations;
	private PropertiesConfiguration propertiesConfiguration;
	private Hashtable<String, ConfigurationTableModel> classAttributes;
	private Hashtable<String,JTable> classAssociations;
	private Boolean validatable;
	private Boolean tableChanged;
	
	
	private String[] associationsColumns = new String[]{"Associations", "Min", "Max"};
	private Object[][] associationsData;
	private JTable associations;

	private String[] attributesColumns = new String[]{"Attributes", "Min", "Max", "MinSize", "MaxSize", "Values"};
	private ConfigurationTableModel selectedAttributes;
	private JTable attributes;

	private String[] classesColumns = new String[]{"Classes", "Min", "Max", "Values"};;
	private ConfigurationTableModel classesConfiguration;
	private JTable classes;

	private String[] basicTypesColumns = new String[]{"Typ", "Min", "Max", "Step", "Values"};
	private ConfigurationTableModel basicTypesConfiguration;
	private JTable basicTypes;

	
	private JTabbedPane tabbedPane;
	private JPanel mainPanel;
	private FlowLayout upperLowerPanelLayout;
	private JPanel mainUpperPanel;
	private JPanel mainLowerPanel;
    
	private JButton openFileButton;
	private JButton saveConfigurationButton;
	private JButton validateButton;
	
	private JComboBox<String> sectionSelectionComboBox;
	//ComboBoxActionListener has to be declared and initialized for a functioning update through its removability
	private ComboBoxActionListener comboBoxActionListener;
	private ListSelectionModel classTableSelectionListener;
	private AttributesTableListener attributesTableListener;
	

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
			propertiesConfiguration = propertiesConfigurations.get(selectedSection);
			
			// TODO: Die Tabellen sollen sich auf die ausgewaehlte Configuration hin aktualisieren.
		}
		
	}
	
	private class BasicTypesTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			String basictype = (String)basicTypes.getValueAt(e.getFirstRow(), 0);
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = basicTypes.getValueAt(row, col);
			System.out.println("Fuer "+basictype
					+" in Zeile "+row+" und Spalte "+col
					+" ist der Wert "+value+" hinzugefuegt worden!");
			if (basictype.equals("Integer")) {
				switch (col) {
					case 1: propertiesConfiguration.setProperty("Integer_min", value); break;
					case 2: propertiesConfiguration.setProperty("Integer_max", value); break;
					case 4: 
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty("Integer", Integer.getInteger(values[0]));
						else
							break;
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty("Integer", Integer.getInteger(values[i]));
						}
						break;
				}
			} else
			if (basictype.equals("Real")) {
				switch (col) {
				case 1: propertiesConfiguration.setProperty("Real_min", value); break;
				case 2: propertiesConfiguration.setProperty("Real_max", value); break;
				case 3: propertiesConfiguration.setProperty("Real_step", value); break;
				case 4: 
					String [] values = preparStringForConfiguration((String) value);
					if (values != null) 
						propertiesConfiguration.setProperty("Real", Integer.getInteger(values[0]));
					else
						break;
					for (int i=1; i < values.length; i++) {
						propertiesConfiguration.addProperty("Real", values[i]);
					}
					break;
				}
			} else
			if (basictype.equals("String")) {
				switch (col) {
					case 1: propertiesConfiguration.setProperty("String_min", value); break;
					case 2: propertiesConfiguration.setProperty("String_max", value); break;
					case 4: 
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty("String", Integer.getInteger(values[0]));
						else
							break;
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty("String", values[i]);
						}
						break;
				}
			}
			tableChanged = true;
		}
	}
	
	private class ClassesTableListener implements TableModelListener {
		
		@Override
		public void tableChanged(TableModelEvent e) {
			String className = (String)classes.getValueAt(e.getFirstRow(), 0);
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = classes.getValueAt(row, col);
			System.out.println("Fuer "+className
					+" in Zeile "+row+" und Spalte "+col
					+" ist der Wert "+value+" hinzugefuegt worden!");
			switch (col) {
			case 1: propertiesConfiguration.setProperty(className + "_min", value); break;
			case 2: propertiesConfiguration.setProperty(className + "_max", value); break;
			case 3: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty(className, Integer.getInteger(values[0]));
				else
					break;
				for (int i=1; i < values.length-1; i++) {
					propertiesConfiguration.addProperty(className, values[i]);
				}
				break;
			}
			tableChanged = true;
		}
		
	}
	
	private class AttributesTableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			String attributeName = (String)attributes.getValueAt(e.getFirstRow(), 0);
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = attributes.getValueAt(row, col);
			System.out.println("Fuer "+attributeName
					+" in Zeile "+row+" und Spalte "+col+1
					+" ist der Wert "+value+" hinzugefuegt worden!");
			switch (col) {
			case 1: propertiesConfiguration.setProperty(attributeName + "_min", value); break;
			case 2: propertiesConfiguration.setProperty(attributeName + "_max", value); break;
			case 3: propertiesConfiguration.setProperty(attributeName + "_minSize", value); break;
			case 4: propertiesConfiguration.setProperty(attributeName + "_maxSize", value); break;
			case 5: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty(attributeName, Integer.getInteger(values[0]));
				else
					break;
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty(attributeName, values[i]);
				}
				break;
			}
			tableChanged = true;
		}
		
	}

	private class AssociationsTableListener implements TableModelListener {
		
		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//TODO: Restliche TableListener machen
	
	class ClassTableSelectionHandler implements ListSelectionListener {
		// TODO: es soll auf die Abwahl einer Klassenzeile reagiert und die
		// Werte der abgewaehlten Klasse in die Hashtabelle classesAttributes und classesAssociations abspeichert werden,
		// und es soll diese gewaehlten Klassenattribute- und assoziatonen in der Attributen- und Assoziationentabelle angezeigt werden
        public void valueChanged(ListSelectionEvent e) { 
        	ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        	int selectedRow = 0;
        	int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                	selectedRow = i;
                }
            }
            String selectedClass = (String) classes.getValueAt(selectedRow, 0);
            fillSelectedAttributes(selectedClass);
            //TODO: fillSelectedAssociations() fehlt noch
        }
    }

	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		associationsColumns = new String[]{"Associations", "Min", "Max"};
		associationsData = new Object[][]{
				{"belongsTo",	1,	2},
				{"owns", 		3,	4},
				{"cowFarmer", 	2,	6}
		};
		associations = new JTable(associationsData, associationsColumns);
		
		selectedAttributes = new ConfigurationTableModel(attributesColumns, new Object[1][6]);
		attributes = new JTable(selectedAttributes);
		
		classesConfiguration = new ConfigurationTableModel(classesColumns, new Object[model.classes().size()][4]); 
		classes = new JTable(classesConfiguration);
		
		basicTypesConfiguration = new ConfigurationTableModel(basicTypesColumns, 
				new Object[][]{
					//{"Boolean", null,	null, 	null,	null},
					{"Integer",	null,	null, 	null,	null},
					{"Real", 	null,	null, 	null,	null},
					{"String", 	null,	null, 	null,	null}
		});
		basicTypes = new JTable(basicTypesConfiguration);

		basicTypesConfiguration.addTableModelListener(new BasicTypesTableListener());
		classesConfiguration.addTableModelListener(new ClassesTableListener());
		classTableSelectionListener = classes.getSelectionModel();
		classTableSelectionListener.addListSelectionListener(new ClassTableSelectionHandler());
		classes.setSelectionModel(classTableSelectionListener);
		attributesTableListener = new AttributesTableListener();
		selectedAttributes.addTableModelListener(attributesTableListener);
		
		
		file = new File(model.filename().replaceAll("\\.use", "") + ".properties");
		propertiesConfigurations = new Hashtable<String, PropertiesConfiguration>();
		classAttributes = new Hashtable<String,ConfigurationTableModel>();
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
        comboBoxActionListener = new ComboBoxActionListener();  
        sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        
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
        			fillConfigurationInBasicTypes(propertiesConfiguration);
        			fillConfigurationInClasses(propertiesConfiguration, model);
        			fillConfigurationInAttributes(propertiesConfiguration, model);
        			//TODO: fillConfigurationInAssociations() hierrein, sobald fertig
        			collectConfigurations(file); //TODO: Zur Zeit notwendig, da chosenPropertiesConfiguration nicht genuegend gefuellt. Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
        			tableChanged = false;
        			System.out.println("File loaded.");
        		} else {System.out.println("Loading file failed.");}
        		
        	}
        } );
        
        saveConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		// Nur fuer Testzwecke:
        		System.out.println(tableChanged);
        		Object test1 = propertiesConfiguration.getInt("Person_min");
        		String [] test2 = propertiesConfiguration.getStringArray("Person");
        		System.out.println(test1 + ", " +test2);
        		System.out.println(model.getClass("Person").allAttributes().toArray()[0].toString().indexOf(':'));
        		System.out.println(propertiesConfiguration.getInt("Person_alter_min"));
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
        fillConfigurationInBasicTypes(propertiesConfiguration);
        fillConfigurationInClasses(propertiesConfiguration, model);
        fillConfigurationInAttributes(propertiesConfiguration, model);
        //TODO: fillConfigurationInAssociations() hierrein, sobald fertig
        collectConfigurations(file); //TODO: Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
        tableChanged = false;
    	
    	this.setContentPane(mainPanel);
    	this.setLocationRelativeTo(parent);
    	this.pack();
    	this.setVisible(true);
    	
	}
		
	@SuppressWarnings("unchecked")
	private void collectConfigurations(File file) {
		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
		sectionSelectionComboBox.removeAllItems();
		propertiesConfigurations = new Hashtable<String, PropertiesConfiguration>();
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
				this.propertiesConfiguration = propertiesConfiguration;
				isFirstConfiguration = false;
			}
			propertiesConfigurations.put(section.toString(), propertiesConfiguration);
			sectionSelectionComboBox.addItem(section.toString());
		}
		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
	}
	
	public PropertiesConfiguration getChosenPropertiesConfiguration() {
		return propertiesConfiguration;
	}
	
	public Boolean isValidatable() {
		return validatable;
	}

	private JPanel createBasicTypesTab() {
		//Testdatenmodel
		JScrollPane basicTypesScrollPane = new JScrollPane(basicTypes);
		
		JPanel basicTypesPanel = new JPanel(new BorderLayout());
		basicTypesPanel.add(new JLabel("Basic Types"), BorderLayout.NORTH);
		basicTypesPanel.add(basicTypesScrollPane, BorderLayout.CENTER);
		return basicTypesPanel;
	}

	private JSplitPane createClassesAndAssociationsTab() {
		JScrollPane classesScrollPane = new JScrollPane(classes);
        JScrollPane attributesScrollPane = new JScrollPane(attributes);
        JScrollPane associationsScrollPane = new JScrollPane(associations);
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
	
	private void fillConfigurationInBasicTypes(PropertiesConfiguration pc) {
		basicTypesConfiguration.setValueAt(pc.getInt("Integer_min"), 0, 1);
		basicTypesConfiguration.setValueAt(pc.getInt("Integer_max"), 0, 2);
		if (pc.getStringArray("Integer").length > 0) {
			basicTypesConfiguration.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("Integer")), 0, 4);
		} else basicTypesConfiguration.setValueAt(null, 0, 4);
		basicTypesConfiguration.setValueAt(pc.getInt("Real_min"), 1, 1);
		basicTypesConfiguration.setValueAt(pc.getInt("Real_max"), 1, 2);
		basicTypesConfiguration.setValueAt(pc.getDouble("Real_step"), 1, 3);
		if (pc.getStringArray("Real").length > 0) {
			basicTypesConfiguration.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("Real")), 1, 4);
		} else basicTypesConfiguration.setValueAt(null, 1, 4);
		basicTypesConfiguration.setValueAt(pc.getInt("String_min"), 2, 1);
		basicTypesConfiguration.setValueAt(pc.getInt("String_max"), 2, 2);
		if (pc.getStringArray("String").length > 0) {
			basicTypesConfiguration.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray("String")), 2, 4);
		} else {
			basicTypesConfiguration.setValueAt(null, 2, 4);
		}
	}
	
	private void fillConfigurationInClasses(PropertiesConfiguration pc, MModel model) {
		Iterator<MClass> classes = model.classes().iterator();
		int row = 0;
		while (classes.hasNext()) {
			String className = classes.next().toString();
			classesConfiguration.setValueAt(className,row,0);
			classesConfiguration.setValueAt(pc.getInt(className+"_min"),row,1);
			classesConfiguration.setValueAt(pc.getInt(className+"_max"),row,2);
			if (pc.getStringArray(className).length > 0) {
				classesConfiguration.setValueAt(prepareConfigurationValuesForTable(pc.getStringArray(className)),row,3);
			} else {
				classesConfiguration.setValueAt(null, row,3);
			}
			row++;
		}
	}
	private void fillConfigurationInAttributes(PropertiesConfiguration pc, MModel model) {
		Iterator<MClass> classes = model.classes().iterator();
		Boolean isFirstClass = true;

		while (classes.hasNext()) {
			int row = 0;
			MClass clazz = classes.next();
			String className = clazz.toString().trim();
			Iterator<MAttribute> attributesIterator = clazz.allAttributes().iterator();
			int attributesCount = clazz.allAttributes().size();
			Object[][] attributesData = new Object[attributesCount][6];
			while (attributesIterator.hasNext()) {
				MAttribute attribute = attributesIterator.next();
				String attributeName = className+"_"+attribute.toString().substring(0, (attribute.toString().indexOf(':')-1)).trim();
				attributesData[row][0] = attributeName;
				attributesData[row][1] = pc.getInt(attributeName+"_min");
				attributesData[row][2] = pc.getInt(attributeName+"_max");
				if (pc.containsKey(attributeName+"_minSize")) {
					attributesData[row][3] = pc.getInt(attributeName+"_minSize");
				}
				if (pc.containsKey(attributeName+"_maxSize")) {
					attributesData[row][4] = pc.getInt(attributeName+"_maxSize");
				}
				if (pc.getStringArray(attributeName).length > 0) {
					attributesData[row][5] = prepareConfigurationValuesForTable(pc.getStringArray(attributeName));
				} else {
					attributesData[row][5] = null;
				}
				row++;
			}
			classAttributes.put(className, new ConfigurationTableModel(attributesColumns,attributesData));
			if (isFirstClass) {
				fillSelectedAttributes(className);
				isFirstClass = false;
			}
		}
	}
	
	private void fillSelectedAttributes(String className) {
		selectedAttributes.removeTableModelListener(attributesTableListener);
		ConfigurationTableModel table = classAttributes.get(className);
		int rowCount = selectedAttributes.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			selectedAttributes.removeRow(0);
		}
		for (int row = 0; row < table.getRowCount(); row++) {
			Object[] tempRow = new Object[table.getColumnCount()];
			for (int col = 0; col < table.getColumnCount(); col++) {
				tempRow[col] = table.getValueAt(row, col);
			}
			selectedAttributes.addRow(tempRow);
		}
		selectedAttributes.addTableModelListener(attributesTableListener);
	}
	
	private void fillConfigurationInAssociationsTable(PropertiesConfiguration pc) {
		//TODO: sich nach fillConfigurationInAttributesTable() richten
	}
		
	private void fillSelectedAssociations(String className) {
		// TODO: Wie bei fillSelectedAttributes()
		// TODO: Wie bei collectConfigurations() soll die Attribute der ersten Klasse gleich in die
		// Attributentabelle ueberfuehrt werden
	}
	
}
