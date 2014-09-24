package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClassImpl;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;


/**
 *  A GUI for the configuration of the model validator
 * 
 * @author Subi Aili
 *
 */
public class ModelValidatorConfigurationWindow extends JDialog {
	

	private static final long serialVersionUID = 1L;
	
	private static final String ASSOCIATIONCLASS_INDICATOR = " [AC]";
	private static final String NON_EDITABLE = "non-editable";
	private static final int OPTIONS_TABLE_DIVIDER_HEIGHT = 2;
	private static final int OPTIONS_TABLE_HEIGHT = 64;
	
	String [] optionsColNames = new String[]{"Options","On","Off"};
	String [] invariantsColNames = new String[]{"Invariants","Active","Inactive","Negate"};
	InvariantsOptionsTable invariants;
	InvariantsOptionsTable options;
	String selectedButton;
	
	private String[] associationsColumns = new String[]{"Associations", "Min", "Max", "Values"};
	private JTable associations;

	private String[] attributesColumns = new String[]{"Attributes", "Min", "Max", "MinSize", "MaxSize", "Values"};
	private JTable attributes;

	private String[] classesColumns = new String[]{"Classes", "Min", "Max", "Values"};;
	private JTable classes;

	private String[] basicTypesColumns = new String[]{"Typ", "Min", "Max", "Step", "Values"};
	private JTable basicTypes;

	private MModel model;
	private File file;
	private HierarchicalINIConfiguration hierarchicalINIConfiguration;
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurations;
	private PropertiesConfiguration propertiesConfiguration;
	private Hashtable<String, ConfigurationTableModel> classAttributes;
	private Hashtable<String, ConfigurationTableModel> classAssociations;
	private String selectedClass;
	private Boolean validatable;
	private Boolean tableChanged;
	
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
	private AssociationsTableListener associationsTableListener;
	
	/*
	 * Listens for changed selection in the drop down menu
	 */
	private class ComboBoxActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO: Wenn an der Konfiguration etwas geaendert wurde,
			// sollte vorher gefragt werden, ob man diese Veraenderungen
			// mit dem Wechsel in einen anderen Konfigurationsektor wirklich verwerfen will.
			// Wenn nicht, wird der Konfigurationssektor nicht gewechselt.
			// Pruefung ob etwas geaendert wurde, kann einfacherweise
			// ueber die Boolean-Variable tableChanged ermittelt werden.

			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>) e.getSource();
			String selectedSection = (String) cb.getSelectedItem();
			propertiesConfiguration = propertiesConfigurations.get(selectedSection);
			
			// TODO: Die Tabellen sollen sich auf die ausgewaehlte Configuration hin aktualisieren.
		}
		
	}
	
	/*
	 * Listens for every change made in the basic types table
	 */
	private class BasicTypesTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			String basictype = (String)basicTypes.getValueAt(e.getFirstRow(), 0);
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = basicTypes.getValueAt(row, col);
			System.out.print("Fuer "+basictype
					+" in Zeile "+row+" und Spalte "+col
					+" soll der Wert "+value+" hinzugefuegt werden!");
			if (basictype.equals("Integer")) {
				switch (col) {
					case 1: propertiesConfiguration.setProperty("Integer_min", value); System.out.println("Geschafft!"); break;
					case 2: propertiesConfiguration.setProperty("Integer_max", value); System.out.println("Geschafft!"); break;
					case 4: 
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty("Integer", Integer.getInteger(values[0]));
						else {
							System.out.println("Geschafft!"); break;
						}
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty("Integer", Integer.getInteger(values[i]));
						}
						System.out.println("Geschafft!"); break;
				}
			} else
			if (basictype.equals("Real")) {
				switch (col) {
				case 1: propertiesConfiguration.setProperty("Real_min", value); System.out.println("Geschafft!"); break;
				case 2: propertiesConfiguration.setProperty("Real_max", value); System.out.println("Geschafft!"); break;
				case 3: propertiesConfiguration.setProperty("Real_step", value); System.out.println("Geschafft!"); break;
				case 4: 
					String [] values = preparStringForConfiguration((String) value);
					if (values != null) 
						propertiesConfiguration.setProperty("Real", Integer.getInteger(values[0]));
					else {
						System.out.println("Geschafft!"); break;
					}
					for (int i=1; i < values.length; i++) {
						propertiesConfiguration.addProperty("Real", Integer.getInteger(values[i]));
					}
					System.out.println("Geschafft!"); break;
				}
			} else
			if (basictype.equals("String")) {
				switch (col) {
					case 1: propertiesConfiguration.setProperty("String_min", value); System.out.println("Geschafft!"); break;
					case 2: propertiesConfiguration.setProperty("String_max", value); System.out.println("Geschafft!"); break;
					case 4: 
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty("String", Integer.getInteger(values[0]));
						else {
							System.out.println("Geschafft!"); break;
						}
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty("String", Integer.getInteger(values[i]));
						}
						System.out.println("Geschafft!"); break;
				}
			}
			tableChanged = true;
		}
	}
	
	/*
	 * Listens for every change made in the class table
	 */
	private class ClassesTableListener implements TableModelListener {
		
		@Override
		public void tableChanged(TableModelEvent e) {
			String eventString = (String)classes.getValueAt(e.getFirstRow(), 0);
			if (!eventString.contains("<html><i>")) { // is like !clazz.isAbstract()
				String className = clearString(eventString);
				int col = e.getColumn();
				int row = e.getFirstRow();
				Object value = classes.getValueAt(row, col);
				System.out.print("name: "+className
						+" row: "+row+" col: "+col
						+" value: "+value);
				switch (col) {
				case 1: 
					if (!isAssociationclass(model.getClass(className)) && !model.getClass(className).isAbstract()) {
						propertiesConfiguration.setProperty(className + "_min", value);
						System.out.println(" done.");
						break;
					} else {
						break;
					}
				case 2: 
					if (!isAssociationclass(model.getClass(className)) && !model.getClass(className).isAbstract()) {
						propertiesConfiguration.setProperty(className + "_max", value);
						System.out.println(" done.");
						break;
					} else {
						break;
					}
				case 3: 
					if (model.getClass(className).isAbstract()) {
						break;
					} else if (!isAssociationclass(model.getClass(className))) {
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty(className, Integer.getInteger(values[0]));
						else {
							System.out.println(" done."); 
							break;
						}
						for (int i=1; i < values.length-1; i++) {
							propertiesConfiguration.addProperty(className, values[i]);
						}
						System.out.println(" done.");  
						break;
					} else {
						String [] values = preparStringForConfiguration((String) value);
						if (values != null) 
							propertiesConfiguration.setProperty(className+"_ac", Integer.getInteger(values[0]));
						else {
							System.out.println(" done."); 
							break;
						}
						for (int i=1; i < values.length-1; i++) {
							propertiesConfiguration.addProperty(className+"_ac", values[i]);
						}
						System.out.println(" done.");  
						break;
						
					}
				}
				tableChanged = true;
			}
		}
		
	}
	
	/*
	 * Listens for every change made in the attribution table
	 */
	private class AttributesTableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			String attributeName = clearString((String)attributes.getValueAt(e.getFirstRow(), 0));
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = attributes.getValueAt(row, col);
			System.out.print("name: "+attributeName
					+" row: "+row+" col: "+col
					+" value "+value);
			switch (col) {
			case 1: propertiesConfiguration.setProperty(attributeName + "_min", value); System.out.println(" done."); break;
			case 2: propertiesConfiguration.setProperty(attributeName + "_max", value); System.out.println(" done."); break;
			case 3: propertiesConfiguration.setProperty(attributeName + "_minSize", value); System.out.println(" done."); break;
			case 4: propertiesConfiguration.setProperty(attributeName + "_maxSize", value); System.out.println(" done."); break;
			case 5: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) {
					propertiesConfiguration.setProperty(attributeName, values[0]);
				} else {
					System.out.println(" done."); 
					break;
				}
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty(attributeName, values[i]);
				}
				break;
			}
			tableChanged = true;
		}
		
	}

	/*
	 * Listens for every change made in the association table
	 */
	private class AssociationsTableListener implements TableModelListener {
		
		@Override
		public void tableChanged(TableModelEvent e) {
			String associationName = clearString((String)associations.getValueAt(e.getFirstRow(), 0));
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = associations.getValueAt(row, col);
			System.out.print("name: "+associationName
					+" row: "+row+" col: "+col
					+" value: "+value);
			switch (col) {
			case 1: propertiesConfiguration.setProperty(associationName + "_min", value); System.out.println(" done."); break;
			case 2: propertiesConfiguration.setProperty(associationName + "_max", value); System.out.println(" done."); break;
			case 3:
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty(associationName, values[0]);
				else {
					System.out.println(" done."); break;
				}
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty(associationName, values[i]);
				}
				System.out.println(" done."); break;
			}
			tableChanged = true;
		}
		
	}
	
	/*
	 * Listens for changed class row selection in the class table
	 */
	class ClassTableSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
        	if (!e.getValueIsAdjusting()) {	
        		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
	        	int selectedRow = 0;
	        	int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	selectedRow = i;
	                }
	            }
	            if (tableChanged) {
	            	updateClassAttributes(selectedClass);
		            updateClassAssociations(selectedClass);
	            }
	            selectedClass = clearString((String) classes.getValueAt(selectedRow, 0));
	            fillAttributeTable(selectedClass);
	            fillAssociationTable(selectedClass);
	        }
        }
    }
	
	private class InvariantsOptionsTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		
		public InvariantsOptionsTableModel(Object rowData[][], String columnNames[]) {
			super(rowData, columnNames);
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			if (col==0) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	private class InvariantsOptionsTable extends JTable {
		private static final long serialVersionUID = 1L;

		public InvariantsOptionsTable(InvariantsOptionsTableModel iotm) {
			super(iotm);
		}
		
		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			if (row >= 0 && col >= 0) {
				System.out.println(getValueAt(row,0)+": "+selectedButton);
				if (selectedButton != null) {
					propertiesConfiguration.setProperty(clearString((String) getValueAt(row,0)), selectedButton.toLowerCase());
					System.out.println("done.");
				}
			}
			super.tableChanged(e);
			repaint();
		}
	}
	
	private class SwitchRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (value == null)
				return null;
			return (Component) value;
		}
	}
	
	private class SwitchEditor extends DefaultCellEditor implements ItemListener {
		private static final long serialVersionUID = 1L;
		
		private JRadioButton button;
		
		public SwitchEditor(JCheckBox checkbox) {
			super(checkbox);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			if (value == null)
				return null;
			button = (JRadioButton) value;
			button.addItemListener(this);
			return (Component) value;
		}
		
		public Object getCellEditorValue() {
			button.removeItemListener(this);
			return button;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			selectedButton = ((AbstractButton) e.getItemSelectable()).getActionCommand();
			super.fireEditingStopped();
		}
		
	}

	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		//TODO: Design durch bessers als das hier verbessern
		this.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//TODO: USE soll weiterhin verwendbar bleiben, waehrrend die MV-GUI weiterlaeuft.
		//Vielleicht durch Einkapselung des ganzen Kodkod-Validationsverfahren als ein Thread?
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(true);
		this.setSize(800,300);

		this.model = model;
		
		associations = new JTable(new ConfigurationTableModel(associationsColumns, new Object[1][4]));
		attributes = new JTable(new ConfigurationTableModel(attributesColumns, new Object[1][6]));
		classes = new JTable(new ConfigurationTableModel(classesColumns, new Object[model.classes().size()][4]));
		basicTypes = new JTable(new ConfigurationTableModel(basicTypesColumns, new Object[3][5]));
		options = new InvariantsOptionsTable(new InvariantsOptionsTableModel(new Object[2][3], optionsColNames));
		invariants = new InvariantsOptionsTable(new InvariantsOptionsTableModel(
				new Object[model.classInvariants().size()][4],invariantsColNames));

		basicTypes.getModel().addTableModelListener(new BasicTypesTableListener());
		classes.getModel().addTableModelListener(new ClassesTableListener());
		classTableSelectionListener = classes.getSelectionModel();
		classTableSelectionListener.addListSelectionListener(new ClassTableSelectionHandler());
		classes.setSelectionModel(classTableSelectionListener);
		attributesTableListener = new AttributesTableListener();
		attributes.getModel().addTableModelListener(attributesTableListener);
		associationsTableListener = new AssociationsTableListener();
		associations.getModel().addTableModelListener(associationsTableListener);
		
		
		file = new File(model.filename().replaceAll("\\.use", "") + ".properties");
		propertiesConfigurations = new Hashtable<String, PropertiesConfiguration>();
		classAttributes = new Hashtable<String,ConfigurationTableModel>();
		classAssociations = new Hashtable<String,ConfigurationTableModel>();
		validatable = false;

    	
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
        			//TODO: Sicherstellen, dass saemtliche Tabellen neu gemalt werden, als wuerde(oder auch genauso) die
        			//GUI neugestartet sein
        			collectConfigurations(file);
        			insertConfigurationInBasicTypes();
        			insertConfigurationInClasses();
        			insertConfigurationInAttributes();
        			insertConfigurationInAssociations();
        			insertConfigurationInInvariantsOptions();
        			collectConfigurations(file); //TODO: Zur Zeit notwendig, da chosenPropertiesConfiguration nicht genuegend gefuellt. Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
        			tableChanged = false;
        			classes.clearSelection();
        			System.out.println("File loaded.");
        		} else {System.out.println("Loading file failed.");}
        		
        	}
        } );
        
        saveConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		//TODO: Die Configuration muss in der .properties-Datei an der richtigen Position
        		//gespeichert werden
        	}
        });
        
        //TODO: Warnung ausgeben dass, falls die Konfiguration nicht abgespeichert(Save-Button gedrueckt) wurde,
        //diese in der folgenden Validierung zwar verwendet wird, danach aber verworfen ist.
        validateButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		validatable = true;
        		setVisible(false);
        	}
        } );
        
        tabbedPane.add("Basic types", createBasicTypesTab());
        tabbedPane.add("Classes and associations", createClassesAndAssociationsTab());
        tabbedPane.add("Invariants and options", createInvariantsAndOptionsTab());
        mainUpperPanel.add(openFileButton);
        mainUpperPanel.add(sectionSelectionComboBox);
        mainUpperPanel.add(saveConfigurationButton);
        mainLowerPanel.add(validateButton);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(mainUpperPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER); 
        mainPanel.add(mainLowerPanel, BorderLayout.SOUTH);

        collectConfigurations(file);
        tableChanged = false;
        insertConfigurationInBasicTypes();
        insertConfigurationInClasses();
        insertConfigurationInAttributes();
        insertConfigurationInAssociations();
        insertConfigurationInInvariantsOptions();
        classes.setRowSelectionInterval(0,0);
        collectConfigurations(file); //TODO: Diese Zeile wegmachen, sobald alle Konfiguration aus allen Tabellen erfolgreich ausgelesen werden koennen
    	
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
			// TODO Exception-Message in einem Dialog-Fenster wiedergeben(bei allen anderen auch so machen wie folgend geloest)
			e.printStackTrace();
			return;
		}
		if (!hierarchicalINIConfiguration.getSections().isEmpty()) {
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
		} else {
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
				String section = "lone_config";
				Iterator<String> keysIterator = hierarchicalINIConfiguration.getKeys();
				while (keysIterator.hasNext()) {
					String key = keysIterator.next();
					if (!key.startsWith("--"))
						propertiesConfiguration.addProperty(key, hierarchicalINIConfiguration.getString(key));
				}
				this.propertiesConfiguration = propertiesConfiguration;
				propertiesConfigurations.put(section, propertiesConfiguration);
				sectionSelectionComboBox.addItem(section);
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
		basicTypes.getModel().setValueAt("Integer",0,0);
		basicTypes.getModel().setValueAt("Real",1,0);
		basicTypes.getModel().setValueAt("String",2,0);
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
	
	private JSplitPane createInvariantsAndOptionsTab() {
		options.getModel().setValueAt(PropertyEntry.aggregationcyclefreeness,0,0);
		options.getModel().setValueAt(new JRadioButton("On"),0,1);
		options.getModel().setValueAt(new JRadioButton("Off"),0,2);
		options.getModel().setValueAt(PropertyEntry.forbiddensharing,1,0);
		options.getModel().setValueAt(new JRadioButton("On"),1,1);
		options.getModel().setValueAt(new JRadioButton("Off"),1,2);
		
		ButtonGroup aCFButtonsA = new ButtonGroup();
		aCFButtonsA.add((JRadioButton) options.getModel().getValueAt(0, 1));
		aCFButtonsA.add((JRadioButton) options.getModel().getValueAt(0, 2));
		ButtonGroup aCFButtonsB = new ButtonGroup();
		aCFButtonsB.add((JRadioButton) options.getModel().getValueAt(1, 1));
		aCFButtonsB.add((JRadioButton) options.getModel().getValueAt(1, 2));
		

		Iterator<MClassInvariant> allInvariantsIterator = model.classInvariants().iterator();
		Hashtable<String,ButtonGroup> buttonGroups = new Hashtable<String,ButtonGroup>();
		int i = 0;
		while (allInvariantsIterator.hasNext()) {
			MClassInvariant inv = allInvariantsIterator.next();
			String invName = inv.cls()+"_"+inv.name();
			System.out.println(invName);
			invariants.getModel().setValueAt(invName,i,0);
			invariants.getModel().setValueAt(new JRadioButton("Active"), i, 1);
			invariants.getModel().setValueAt(new JRadioButton("Inactive"), i, 2);
			invariants.getModel().setValueAt(new JRadioButton("Negate"), i, 3);
			
			buttonGroups.put(invName,new ButtonGroup());
			buttonGroups.get(invName).add((AbstractButton) invariants.getModel().getValueAt(i, 1));
			buttonGroups.get(invName).add((AbstractButton) invariants.getModel().getValueAt(i, 2));
			buttonGroups.get(invName).add((AbstractButton) invariants.getModel().getValueAt(i, 3));
			
			i++;
		}	
		
		options.getColumn("On").setCellRenderer(new SwitchRenderer());
		options.getColumn("Off").setCellRenderer(new SwitchRenderer());
		invariants.getColumn("Active").setCellRenderer(new SwitchRenderer());
		invariants.getColumn("Inactive").setCellRenderer(new SwitchRenderer());
		invariants.getColumn("Negate").setCellRenderer(new SwitchRenderer());
		
		options.getColumn("On").setCellEditor(new SwitchEditor(new JCheckBox()));
		options.getColumn("Off").setCellEditor(new SwitchEditor(new JCheckBox()));
		invariants.getColumn("Active").setCellEditor(new SwitchEditor(new JCheckBox()));
		invariants.getColumn("Inactive").setCellEditor(new SwitchEditor(new JCheckBox()));
		invariants.getColumn("Negate").setCellEditor(new SwitchEditor(new JCheckBox()));
		
		options.setPreferredScrollableViewportSize(new Dimension(350,options.getRowHeight()*options.getRowCount()));
		options.getColumnModel().getColumn(0).setPreferredWidth(200);
		invariants.setPreferredScrollableViewportSize(new Dimension(800,invariants.getRowHeight()*invariants.getRowCount()));
		invariants.getColumnModel().getColumn(0).setPreferredWidth(400);
		
		JPanel optionsPanel = new JPanel();
		JPanel invariantsPanel = new JPanel();
		JSplitPane ioPane;
		
		optionsPanel.add(new JScrollPane(options));
		invariantsPanel.add(new JScrollPane(invariants));
		ioPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, optionsPanel, invariantsPanel);
		ioPane.setDividerLocation(OPTIONS_TABLE_HEIGHT);
		ioPane.setDividerSize(OPTIONS_TABLE_DIVIDER_HEIGHT);
		
		return ioPane;
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
		} else { 
			return null;
		}
	}
	
	private String prepareConfigurationValuesForTable(Object arrayList) {
		String string;
		if (!arrayList.getClass().equals(String.class)) {
			string = arrayList.toString().trim();
			return string.substring(5, string.length()-2);
		} else {
			string = (String) arrayList;
			string = string.trim();
			return string.substring(4,string.length()-1);
		}
	}
	
	/**
	 * fills gui table for basic types with the values from the chosen propertiesConfiguration
	 * @param pc
	 */
	private void insertConfigurationInBasicTypes() {
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("Integer_min"), 0, 1);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("Integer_max"), 0, 2);
		if (!(propertiesConfiguration.getProperty("Integer") == null)) {
			basicTypes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("Integer")), 0, 4);
		} else basicTypes.getModel().setValueAt(null, 0, 4);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("Real_min"), 1, 1);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("Real_max"), 1, 2);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getDouble("Real_step"), 1, 3);
		if (!(propertiesConfiguration.getProperty("Real") == null)) {
			basicTypes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("Real")), 1, 4);
		} else basicTypes.getModel().setValueAt(null, 1, 4);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("String_min"), 2, 1);
		basicTypes.getModel().setValueAt(propertiesConfiguration.getInt("String_max"), 2, 2);
		if (!(propertiesConfiguration.getProperty("String") == null)) {
			basicTypes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("String")), 2, 4);
		} else {
			basicTypes.getModel().setValueAt(null, 2, 4);
		}
	}
	
	private Boolean isAssociationclass(MClass clazz) {
		if (clazz.getClass().equals(MAssociationClassImpl.class)) {
			return true;
		}
		return false;
	}
	
	private Boolean isAssociationclass(MAssociation association) {
		if (association.getClass().equals(MAssociationClassImpl.class)) {
			return true;
		}
		return false;
	}
	
	/**
	 * fills gui table for classes with the classnames and their referenced values
	 * and formats the classnames appropriately
	 * @param pc
	 * @param model
	 */
	private void insertConfigurationInClasses() {
		Iterator<MClass> classesIterator = model.classes().iterator();
		int row = 0;
		while (classesIterator.hasNext()) {
			MClass clazz = classesIterator.next();
			String className = clazz.toString();
			if (isAssociationclass(clazz)) {
				if (clazz.isAbstract()) {
					classes.getModel().setValueAt( html(italic(className+bold(ASSOCIATIONCLASS_INDICATOR))) ,row,0);
				} else {
					classes.getModel().setValueAt( html(className+italic(bold(ASSOCIATIONCLASS_INDICATOR))) ,row,0);
					classes.getModel().setValueAt(NON_EDITABLE,row,1);
					classes.getModel().setValueAt(NON_EDITABLE,row,2);
				}
			} else {
				if (clazz.isAbstract()) {
					classes.getModel().setValueAt( html(italic(className)) ,row,0);
				} else {
					classes.getModel().setValueAt(className,row,0);
				}
			}
			if (clazz.isAbstract()) {
				classes.getModel().setValueAt(NON_EDITABLE,row,1);
				classes.getModel().setValueAt(NON_EDITABLE,row,2);
				classes.getModel().setValueAt(NON_EDITABLE,row,3);
				row++;
				continue;
			}
			if (!isAssociationclass(clazz)) {
				if (propertiesConfiguration.containsKey(className+"_min")) {
					classes.getModel().setValueAt(propertiesConfiguration.getInt(className+"_min"),row,1);
				}
				if (propertiesConfiguration.containsKey(className+"_max")) {
					classes.getModel().setValueAt(propertiesConfiguration.getInt(className+"_max"),row,2);
				}
				if (propertiesConfiguration.containsKey(className)) {
					if (propertiesConfiguration.getProperty(className) != null) {
						classes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(className)),row,3);
					} else {
						classes.getModel().setValueAt(null, row,3);
					}
				}
			} else {
				if (propertiesConfiguration.containsKey(className+"_ac")) {
					if (propertiesConfiguration.getProperty(className) != null) {
						classes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(className+"_ac")),row,3);
					} else {
						classes.getModel().setValueAt(null, row,3);
					}
				}
				
			}
			row++;
		}
	}
	
	/**
	 * fills the hashtable classAttributes with attributes mapped to their referenced classes
	 * @param pc
	 * @param model
	 */
	private void insertConfigurationInAttributes() {
		Iterator<MClass> classes = model.classes().iterator();
		Boolean isFirstClass = true;

		while (classes.hasNext()) {
			MClass clazz = classes.next();
			String className = clazz.toString().trim();
			Iterator<MAttribute> attributesIterator = clazz.attributes().iterator();
			int attributesCount = 0;
			for (MAttribute attribute : clazz.attributes()) {
				if (!attribute.isDerived()) {
					attributesCount++;
				}
			}
			Object[][] attributesData = new Object[attributesCount][6];
			int row = 0;
			for(MAttribute attribute : clazz.attributes()){
				if(attribute.isDerived()){
					continue;
				}
				attribute = attributesIterator.next();
				String attributeName = className+"_"+attribute.toString().substring(0, (attribute.toString().indexOf(':')-1)).trim();
				attributesData[row][0] = attributeName;
				if (propertiesConfiguration.containsKey(attributeName+"_min")) {
					attributesData[row][1] = propertiesConfiguration.getInt(attributeName+"_min");
				}
				if (propertiesConfiguration.containsKey(attributeName+"_max")) {
					attributesData[row][2] = propertiesConfiguration.getInt(attributeName+"_max");
				}
				if (propertiesConfiguration.containsKey(attributeName+"_minSize")) {
					attributesData[row][3] = propertiesConfiguration.getInt(attributeName+"_minSize");
				}
				if (propertiesConfiguration.containsKey(attributeName+"_maxSize")) {
					attributesData[row][4] = propertiesConfiguration.getInt(attributeName+"_maxSize");
				}
				if (propertiesConfiguration.containsKey(attributeName)) {
					if (propertiesConfiguration.getProperty(attributeName) != null) {
						attributesData[row][5] = prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(attributeName));
					} else {
						attributesData[row][5] = null;
					}
				}
				row++;
			}

			classAttributes.put(className, new ConfigurationTableModel(attributesColumns,attributesData));
			if (isFirstClass) {
				selectedClass = className;
				fillAttributeTable(className);
				isFirstClass = false;
			}
		}
	}
	
	/**
	 * fills the gui table for attributes with values mapped with passed className
	 * @param className
	 */
	private void fillAttributeTable(String className) {
		attributes.getModel().removeTableModelListener(attributesTableListener);
		ConfigurationTableModel table = classAttributes.get(className);
		int rowCount = attributes.getModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			((ConfigurationTableModel) attributes.getModel()).removeRow(0);
		}
		for (int row = 0; row < table.getRowCount(); row++) {
			Object[] tempRow = new Object[table.getColumnCount()];
			for (int col = 0; col < table.getColumnCount(); col++) {
				tempRow[col] = table.getValueAt(row, col);
			}
			((ConfigurationTableModel) attributes.getModel()).addRow(tempRow);
		}
		attributes.getModel().addTableModelListener(attributesTableListener);
	}
	
	/**
	 * updates the attributes table with changed values found in the chosen properties configuration
	 * @param className
	 */
	private void updateClassAttributes(String className) {
		ConfigurationTableModel table = classAttributes.get(className);
		for (int row = 0; row < table.getRowCount(); row++) {
			String attributeName = (String)table.getValueAt(row, 0);
			if (propertiesConfiguration.containsKey(attributeName+"_min")) {
				table.setValueAt(propertiesConfiguration.getInt(attributeName+"_min"), row, 1);
			}
			if (propertiesConfiguration.containsKey(attributeName+"_max")) {
				table.setValueAt(propertiesConfiguration.getInt(attributeName+"_max"), row, 2);
			}
			if (propertiesConfiguration.containsKey(attributeName+"_minSize")) {
				table.setValueAt(propertiesConfiguration.getInt(attributeName+"_minSize"), row, 3);
			}
			if (propertiesConfiguration.containsKey(attributeName+"_maxSize")) {
				table.setValueAt(propertiesConfiguration.getInt(attributeName+"_maxSize"), row, 4);
			}
			if (propertiesConfiguration.containsKey(attributeName)) {
				if (propertiesConfiguration.getProperty(attributeName) != null) {
					table.setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(attributeName)), row, 5);
				} else {
					table.setValueAt(null, row, 5);
				}
			}
		}
	}
	
	/**
	 * fills the hashtable classAssociations with associations referenced to their
	 * first association end
	 * @param pc
	 * @param model
	 */
	private void insertConfigurationInAssociations() {
		Iterator<MClass> classes = model.classes().iterator();
		Boolean isFirstClass = true;

		while (classes.hasNext()) {
			MClass clazz = classes.next();
			String className = clearString(clazz.toString());
			int classAssociationsCount = 0;
			Iterator<MAssociation> associationsIterator = model.associations().iterator();
			while (associationsIterator.hasNext()) {
				MAssociation association = associationsIterator.next();
				if (association.associationEnds().iterator().next().cls().equals(clazz) || clazz.equals(association)) {
					classAssociationsCount++;
				}
			}
			Object[][] associationsData = new Object[classAssociationsCount][4];
			String associationName;
			associationsIterator = model.associations().iterator();
			int row = 0;
			while (associationsIterator.hasNext() && (row < classAssociationsCount)) {
				MAssociation association = associationsIterator.next();
				if ((association.associationEnds().iterator().next().cls().equals(clazz))
						|| isAssociationclass(clazz)) {
					associationName = association.toString().trim();
					if (isAssociationclass(association) && association.isAbstract()) {
						associationsData[row][0] = html(italic(associationName+bold(ASSOCIATIONCLASS_INDICATOR)));
					} else if (isAssociationclass(association) && !association.isAbstract()) {
						associationsData[row][0] = html(associationName+italic(bold(ASSOCIATIONCLASS_INDICATOR)));
					} else if (!isAssociationclass(association) && association.isAbstract()) {
						associationsData[row][0] = html(italic(associationName));
					} else {
						associationsData[row][0] = associationName;
					}
					if (propertiesConfiguration.containsKey(associationName+"_min")) {
						associationsData[row][1] = propertiesConfiguration.getInt(associationName+"_min");
					}
					if (propertiesConfiguration.containsKey(associationName+"_max")) {
						associationsData[row][2] = propertiesConfiguration.getInt(associationName+"_max");
					}
					if (propertiesConfiguration.containsKey(associationName)) {
						if (propertiesConfiguration.getProperty(associationName) != null) {
							associationsData[row][3] = prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(associationName));
						} else {
							associationsData[row][3] = null;
						}
					}
					row++;
				}
			}
			classAssociations.put(className, new ConfigurationTableModel(associationsColumns,associationsData));
			if (isFirstClass) {
				fillAssociationTable(className);
				isFirstClass = false;
			}
		}
	}
		
	/**
	 * fills the gui table for associations with values mapped with passed parameter
	 * @param className
	 */
	private void fillAssociationTable(String className) {
		associations.getModel().removeTableModelListener(associationsTableListener);
		ConfigurationTableModel table = classAssociations.get(className.trim());
		int rowCount = associations.getModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			((ConfigurationTableModel) associations.getModel()).removeRow(0);
		}
		for (int row = 0; row < table.getRowCount(); row++) {
			Object[] tempRow = new Object[table.getColumnCount()];
			for (int col = 0; col < table.getColumnCount(); col++) {
				tempRow[col] = table.getValueAt(row, col);
			}
			((ConfigurationTableModel) associations.getModel()).addRow(tempRow);
		}
		associations.getModel().addTableModelListener(associationsTableListener);
	}
	
	/**
	 * updates the attributes table with changed values found in the chosen properties configuration
	 * @param className
	 */
	private void updateClassAssociations(String className) {
		ConfigurationTableModel table = classAssociations.get(className);
		for (int row = 0; row < table.getRowCount(); row++) {
			String associationName = clearString((String)table.getValueAt(row, 0));
			if (propertiesConfiguration.containsKey(associationName+"_min")) {
				table.setValueAt(propertiesConfiguration.getInt(associationName+"_min"), row, 1);
			}
			if (propertiesConfiguration.containsKey(associationName+"_max")) {
				table.setValueAt(propertiesConfiguration.getInt(associationName+"_max"), row, 2);
			}
			if (propertiesConfiguration.containsKey(associationName)) {
				if (propertiesConfiguration.getProperty(associationName) != null) {
					table.setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(associationName)), row, 3);
				} else {
					table.setValueAt(null, row, 3);
				}
			}
		}
	}
	
	private void insertConfigurationInInvariantsOptions() {
		if ((propertiesConfiguration.containsKey(PropertyEntry.aggregationcyclefreeness)) && (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness) != null)) {
			if (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness).equalsIgnoreCase("on")) {
				((JRadioButton) options.getModel().getValueAt(0,1)).setSelected(true);
			} else if (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness).equalsIgnoreCase("off")) {
				((JRadioButton) options.getModel().getValueAt(0,2)).setSelected(true);
			} else {
				System.out.println("Wrong value for aggregationcyclefreeness; it must be \"on\" or \"off\""); //TODO: Exception ausgeben? Fehler loggen?
			}
		} else {
			((JRadioButton) options.getModel().getValueAt(0,2)).setSelected(true);
		}
		if ((propertiesConfiguration.containsKey(PropertyEntry.forbiddensharing)) && (propertiesConfiguration.getString(PropertyEntry.forbiddensharing) != null)) {
			if (propertiesConfiguration.getString(PropertyEntry.forbiddensharing).equalsIgnoreCase("on")) {
				((JRadioButton) options.getModel().getValueAt(1,1)).setSelected(true);
			} else if (propertiesConfiguration.getString(PropertyEntry.forbiddensharing).equalsIgnoreCase("off")) {
				((JRadioButton) options.getModel().getValueAt(1,2)).setSelected(true);
			} else {
				System.out.println("Wrong value for forbiddensharing; it must be \"on\" or \"off\"."); //TODO: Exception ausgeben? Fehler loggen?
			}
		} else {
			((JRadioButton) options.getModel().getValueAt(1,1)).setSelected(true);
		}
		
		int invCount = model.classInvariants().size();
		for (int i = 0; i < invCount; i++) {
			String invNameOfRow = (String) invariants.getModel().getValueAt(i, 0);
			if ((propertiesConfiguration.containsKey(invNameOfRow)) && (propertiesConfiguration.getString(invNameOfRow) != null) ) {
				if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("active")) {
					((JRadioButton) invariants.getModel().getValueAt(i, 1)).setSelected(true);
				} else if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("inactive")) {
					((JRadioButton) invariants.getModel().getValueAt(i, 2)).setSelected(true);
				} else if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("negate")) {
					((JRadioButton) invariants.getModel().getValueAt(i, 3)).setSelected(true);
				} else {
					System.out.println("Wrong value for "+invNameOfRow+"; it must be \"active\", \"inactive\" or \"negate\".");
				}
			} else {
				((JRadioButton) invariants.getModel().getValueAt(i, 2)).setSelected(true);
			}
		}
	}
	
	private String html(String string) {
		return "<html>"+string+"</html>";
	}
	
	private String bold(String string) {
		return "<b>"+string+"</b>";
	}
	
	private String italic(String string) {
		return "<i>"+string+"</i>";
	}
	
	/**
	 * @param string
	 * @return string cleaned from html-tags and a specified string ending 
	 */
	private String clearString(String string) {
		if (string.contains("<html>")) {
			String temp = string.replaceAll("\\<[^>]*>","");
			if (temp.contains(ASSOCIATIONCLASS_INDICATOR)) {
				return temp.substring(0,temp.indexOf(ASSOCIATIONCLASS_INDICATOR)).trim();
			} else {
				return temp.trim();
			}
		} else if (string.contains(ASSOCIATIONCLASS_INDICATOR)) {
				return string.substring(0,string.indexOf(ASSOCIATIONCLASS_INDICATOR)).trim();
		} else {
				return string.trim();
		}
	}

}
