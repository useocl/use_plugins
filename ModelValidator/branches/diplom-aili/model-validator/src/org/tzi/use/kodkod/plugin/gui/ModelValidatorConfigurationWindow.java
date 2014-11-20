package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
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
public class ModelValidatorConfigurationWindow extends JDialog implements MouseMotionListener {
	

	private static final long serialVersionUID = 1L;
	
	private static final String ASSOCIATIONCLASS_INDICATOR = " [AC]";
	//TODO: Alle nicht editierbaren Tabellenzellen ausgrauen, statt nur "non-editable" zu schreiben
	private static final String NON_EDITABLE = "non-editable";
	private static final int OPTIONS_TABLE_DIVIDER_HEIGHT = 2;
	private static final int OPTIONS_TABLE_HEIGHT = 64;
	
	int defaultNameCount = 0;
	
	String selectedSection;
	String [] optionsColNames = new String[]{"Options","On","Off"};
	String [] invariantsColNames = new String[]{"Invariants","Active","Inactive","Negate"};
	InvariantsOptionsTable invariants;
	InvariantsOptionsTable options;
	String selectedButton;
	JLabel currentFileLabel;
	JTextArea explainArea;
	
	private String[] associationsColumns = new String[]{ConfigurationConversion.ASSOCIATIONS, ConfigurationConversion.ASSOCIATIONS_MIN, ConfigurationConversion.ASSOCIATIONS_MAX, ConfigurationConversion.ASSOCIATIONS_VALUES};
	private JTable associations;

	private JTable attributes;

	private String[] classesColumns = new String[]{ConfigurationConversion.CLASSES, ConfigurationConversion.CLASSES_MIN, ConfigurationConversion.CLASSES_MAX, ConfigurationConversion.CLASSES_VALUES};;
	private JTable classes;

	private String[] intTypeColumns = new String[]{ConfigurationConversion.BASIC_TYPE, ConfigurationConversion.INTEGER_MIN, ConfigurationConversion.INTEGER_MAX, ConfigurationConversion.INTEGER_VALUES};
	private String[] realTypeColumns = new String[]{ConfigurationConversion.BASIC_TYPE, ConfigurationConversion.REAL_MIN, ConfigurationConversion.REAL_MAX, ConfigurationConversion.REAL_STEP, ConfigurationConversion.REAL_VALUES};
	private String[] stringTypeColumns = new String[]{ConfigurationConversion.BASIC_TYPE, ConfigurationConversion.STRING_MIN, ConfigurationConversion.STRING_MAX, ConfigurationConversion.STRING_VALUES};
	private JTable intConf;
	private JTable realConf;
	private JTable stringConf;

	private MModel model;
	private File file;
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurationSections;
	private PropertiesConfiguration propertiesConfiguration;
	private Hashtable<String, ConfigurationTableModel> classAttributes;
	private Hashtable<String, ConfigurationTableModel> classAssociations;
	private String selectedClass;
	private Boolean validatable;
	private Boolean configurationChanged;
	
	private JTabbedPane center;
	private JPanel main;
	private FlowLayout upperLowerPanelLayout;
	private JPanel northNorth;
	private JPanel northSouth;
	private JPanel north;
	private JPanel southWest;
	private JPanel southCenter;
	private JPanel south;
    
	private JButton openFileButton;
	private JButton saveConfigurationButton;
	private JButton renameConfigurationButton;
	private JButton deleteConfigurationButton;
	private JButton newConfigurationButton;
	private JButton validateButton;
	
	private JComboBox<String> sectionSelectionComboBox;
	//ComboBoxActionListener has to be declared and initialized for a functioning update through its removability
	private ComboBoxActionListener comboBoxActionListener;
	private ListSelectionModel classTableSelectionListener;
	private AttributesTableListener attributesTableListener;
	private AssociationsTableListener associationsTableListener;

	
	private ValidationConfiguration config;
	
	/*
	 * Listens for changed selection in the drop down menu, puts previous propertiesConfiguration
	 * into propertiesConfigurationSection with former selectedSection, then gets the current
	 * selected Section and get its propertiesConfiguration, eventually puts the loaded configurations
	 * into the tables.
	 */
	private class ComboBoxActionListener implements ActionListener {
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			if (propertiesConfiguration != null) {
				propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
			}
			selectedSection = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
			propertiesConfiguration = (PropertiesConfiguration) propertiesConfigurationSections.get(selectedSection).clone();
			insertConfigurationInBasicTypes();
			insertConfigurationInClasses();
			insertConfigurationInAttributes();
			insertConfigurationInAssociations();
			insertConfigurationInInvariantsOptions();
			configurationChanged = false;
			classes.repaint();
			classes.clearSelection();
			System.out.println("Configuration loaded.");
		}
		
	}
	
	/*
	 * Listens for every change made in the int type table
	 */
	private class IntTypeTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = intConf.getValueAt(row, col);
			
			switch (col) {
			case 1: propertiesConfiguration.setProperty("Integer_min", value); break;
			case 2: propertiesConfiguration.setProperty("Integer_max", value); break;
			case 3: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty("Integer", Integer.getInteger(values[0]));
				else {
					break;
				}
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty("Integer", Integer.getInteger(values[i]));
				}
				break;
			}
			configurationChanged = true;
		}
	}
	
	/*
	 * Listens for every change made in the real type table
	 */
	private class RealTypeTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = realConf.getValueAt(row, col);
			
			switch (col) {
			case 1: propertiesConfiguration.setProperty("Real_min", value); break;
			case 2: propertiesConfiguration.setProperty("Real_max", value); break;
			case 3: propertiesConfiguration.setProperty("Real_step", value); break;
			case 4: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty("Real", Integer.getInteger(values[0]));
				else {
					break;
				}
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty("Real", Integer.getInteger(values[i]));
				}
				break;
			}
			configurationChanged = true;
		}
	}
	
	/*
	 * Listens for every change made in the string type table
	 */
	private class StringTypeTableListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object value = stringConf.getValueAt(row, col);
			
			switch (col) {
			case 1: propertiesConfiguration.setProperty("String_min", value); break;
			case 2: propertiesConfiguration.setProperty("String_max", value); break;
			case 3: 
				String [] values = preparStringForConfiguration((String) value);
				if (values != null) 
					propertiesConfiguration.setProperty("String", Integer.getInteger(values[0]));
				else {
					break;
				}
				for (int i=1; i < values.length; i++) {
					propertiesConfiguration.addProperty("String", Integer.getInteger(values[i]));
				}
				break;
			}
			
			configurationChanged = true;
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
						if (values != null) {
							propertiesConfiguration.setProperty(className, values[0]);
							System.out.println("Erster Wert: "+values[0]);
						} else {
							System.out.println(" is empty."); 
							break;
						}
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty(className, values[i]);
							System.out.println("Folgender Wert: "+values[i]);
						}
						System.out.println(" done.");  
						break;
					} else {
						String [] values = preparStringForConfiguration((String) value);
						if (values != null)  {
							propertiesConfiguration.setProperty(className+"_ac", values[0]);
							System.out.println("Erster Wert: "+values[0]);
						} else {
							System.out.println(" is empty."); 
							break;
						}
						for (int i=1; i < values.length; i++) {
							propertiesConfiguration.addProperty(className+"_ac", values[i]);
						}
						System.out.println(" done.");  
						break;
						
					}
				}
				configurationChanged = true;
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
			configurationChanged = true;
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
			configurationChanged = true;
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
	            if (configurationChanged) {
	            	updateClassAttributes(selectedClass);
		            updateClassAssociations(selectedClass);
	            }
	            selectedClass = clearString((String) classes.getValueAt(selectedRow, 0));
	            updateClassAttributes(selectedClass);
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
		
		this.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//TODO: USE soll weiterhin verwendbar bleiben, waehrrend die MV-GUI weiterlaeuft. Dies soll geschehen, indem die Validierung
		// im Thread der MV-GUI mit ausgefuehrt wird.
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(true);
		this.setSize(800,300);

		this.model = model;
		this.config = new ValidationConfiguration(model);
		
		explainArea = new JTextArea();
		associations = new JTable(new ConfigurationTableModel(associationsColumns, new Object[1][4]));
		attributes = new JTable(new AttributeTableModel());
		classes = new JTable(new ConfigurationTableModel(classesColumns, new Object[model.classes().size()][4]));
		intConf = new JTable(new ConfigurationTableModel(intTypeColumns, new Object[1][4]));
		realConf = new JTable(new ConfigurationTableModel(realTypeColumns, new Object[1][5]));
		stringConf = new JTable(new ConfigurationTableModel(stringTypeColumns, new Object[1][4]));
		options = new InvariantsOptionsTable(new InvariantsOptionsTableModel(new Object[2][3], optionsColNames));
		invariants = new InvariantsOptionsTable(new InvariantsOptionsTableModel(
				new Object[model.classInvariants().size()][4],invariantsColNames));
		
		intConf.setName("intConf");
		realConf.setName("realConf");
		stringConf.setName("stringConf");
		classes.setName("classes");
		attributes.setName("attributes");
		associations.setName("associations");
		
		classes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attributes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		associations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		options.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		invariants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		intConf.getModel().addTableModelListener(new IntTypeTableListener());
		realConf.getModel().addTableModelListener(new RealTypeTableListener());
		stringConf.getModel().addTableModelListener(new StringTypeTableListener());
		
		classes.getModel().addTableModelListener(new ClassesTableListener());
		classTableSelectionListener = classes.getSelectionModel();
		
		classTableSelectionListener.addListSelectionListener(new ClassTableSelectionHandler());
		
		classes.setSelectionModel(classTableSelectionListener);
		attributesTableListener = new AttributesTableListener();
		attributes.getModel().addTableModelListener(attributesTableListener);
		associationsTableListener = new AssociationsTableListener();
		associations.getModel().addTableModelListener(associationsTableListener);
		
		this.addMouseMotionListener(this);
		intConf.addMouseMotionListener(this);
		intConf.getTableHeader().addMouseMotionListener(this);
		realConf.addMouseMotionListener(this);
		realConf.getTableHeader().addMouseMotionListener(this);
		stringConf.addMouseMotionListener(this);
		stringConf.getTableHeader().addMouseMotionListener(this);
		classes.addMouseMotionListener(this);
		classes.getTableHeader().addMouseMotionListener(this);
		attributes.addMouseMotionListener(this);
		attributes.getTableHeader().addMouseMotionListener(this);
		associations.addMouseMotionListener(this);
		associations.getTableHeader().addMouseMotionListener(this);
		options.addMouseMotionListener(this);
		invariants.addMouseMotionListener(this);
		
		file = new File(model.filename().replaceAll("\\.use", "") + ".properties");
		currentFileLabel = new JLabel(file.getAbsolutePath());
		propertiesConfigurationSections = new Hashtable<String, PropertiesConfiguration>();
		classAttributes = new Hashtable<String,ConfigurationTableModel>();
		classAssociations = new Hashtable<String,ConfigurationTableModel>();
		validatable = false;
    	
    	center = new JTabbedPane(JTabbedPane.TOP);
    	main = new JPanel();
        upperLowerPanelLayout = new FlowLayout(FlowLayout.LEFT);
        northNorth = new JPanel(upperLowerPanelLayout);
        northSouth = new JPanel(upperLowerPanelLayout);
        north = new JPanel(new BorderLayout());
        southWest = new JPanel(upperLowerPanelLayout);
        southCenter = new JPanel(new BorderLayout());
        southCenter.setBorder(new BevelBorder(BevelBorder.LOWERED));
        south = new JPanel(new BorderLayout());

        openFileButton = new JButton("Open");
        sectionSelectionComboBox = new JComboBox<String>();
        saveConfigurationButton = new JButton("Save");
        renameConfigurationButton = new JButton("Rename");
        deleteConfigurationButton = new JButton("Delete");
        newConfigurationButton = new JButton("New");
        validateButton = new JButton("Close and validate");

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
        		fileChooser.setFileFilter(new ExtFileFilter("properties", "Properties files"));

        		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
        			file = fileChooser.getSelectedFile();
        			collectConfigurations(file);
        			insertConfigurationInBasicTypes();
        			insertConfigurationInClasses();
        			insertConfigurationInAttributes();
        			insertConfigurationInAssociations();
        			insertConfigurationInInvariantsOptions();
        			configurationChanged = false;
        			classes.clearSelection();
        			currentFileLabel.setText(file.getAbsolutePath());
        		} else {
        			JOptionPane.showMessageDialog(getParent(), new JLabel("Error while loading properties file!"), "Error!", JOptionPane.ERROR_MESSAGE);
        		}
        		
        	}
        } );
        
        saveConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
        		saveConfigurationsToFile();
        	}
        });
        
        renameConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		String newName = JOptionPane.showInputDialog("Please input the new name of this configuration:", selectedSection);
        		propertiesConfigurationSections.put(newName, (PropertiesConfiguration) propertiesConfiguration.clone());
        		propertiesConfigurationSections.remove(sectionToDelete);
        		selectedSection = newName;
        		configurationChanged = true;
        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.addItem(newName);
        		sectionSelectionComboBox.removeItem(sectionToDelete);
        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.setSelectedItem(newName);
        	}
        });
        
        deleteConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		propertiesConfigurationSections.remove(sectionToDelete);
        		configurationChanged = true;
        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.removeItem(sectionToDelete);
        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.setSelectedIndex(0);
        	}
        });
        
        newConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String newName = JOptionPane.showInputDialog("Please input the name of the new configuration:", "config"+defaultNameCount);
        		defaultNameCount++;
        		if (newName != null) {
        			propertiesConfigurationSections.put(newName, (PropertiesConfiguration) propertiesConfiguration.clone());
        		} else {
        			return;
        		}
        		selectedSection = newName;
        		configurationChanged = true;
        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.addItem(newName);
        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.setSelectedItem(newName);
        	}
        });
        
        //TODO: Warnung ausgeben dass, falls die Konfiguration nicht abgespeichert(Save-Button gedrueckt) wurde,
        //diese in der folgenden Validierung zwar verwendet wird, danach aber verworfen ist.
        //Das hier entfernen, wenn die Validierung in einem Thread mit der GUI ausgefuert wird.
        validateButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		validatable = true;
        		setVisible(false);
        	}
        } );
        
        northNorth.add(openFileButton);
        northNorth.add(sectionSelectionComboBox);
        northNorth.add(saveConfigurationButton);
        northNorth.add(renameConfigurationButton);
        northNorth.add(deleteConfigurationButton);
        northNorth.add(newConfigurationButton);
        northSouth.add(new JLabel("Properties file loaded: "));
        northSouth.add(currentFileLabel);
        north.add(northNorth, BorderLayout.NORTH);
        north.add(northSouth, BorderLayout.SOUTH);
        center.add("Basic types", createBasicTypesAndOptionsTab());
        center.add("Classes and associations", createClassesAndAssociationsTab());
        center.add("Invariants and options", createInvariantsTab());
        southWest.add(validateButton);
        southCenter.add(explainArea, BorderLayout.CENTER);
        south.add(southWest, BorderLayout.WEST);
        south.add(southCenter, BorderLayout.CENTER);
        main.setLayout(new BorderLayout());
        main.add(north, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER); 
        main.add(south, BorderLayout.SOUTH);
        
        collectConfigurations(file);
        configurationChanged = false;
        insertConfigurationInBasicTypes();
        insertConfigurationInClasses();
        insertConfigurationInAttributes();
        insertConfigurationInAssociations();
        insertConfigurationInInvariantsOptions();
        classes.setRowSelectionInterval(0,0);
        
        explainArea.setEditable(false);
        explainArea.setBackground(getParent().getBackground());
        explainArea.setLineWrap(true);
        explainArea.setWrapStyleWord(true);

        this.setContentPane(main);
    	this.setLocationRelativeTo(parent);
    	this.pack();
    	this.setVisible(true);
	}
		
	/**
	 * extracts all configuration from a .properties-File and puts them in the
	 * hashtable hierarchicalINIConfiguration and sets the first read configuration as
	 * selected propertiesConfiguration
	 * @param file
	 */
	@SuppressWarnings("unchecked")
	private void collectConfigurations(File file) {
		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
		sectionSelectionComboBox.removeAllItems();
		HierarchicalINIConfiguration hierarchicalINIConfiguration;
		propertiesConfigurationSections = new Hashtable<String, PropertiesConfiguration>();
		try {
			hierarchicalINIConfiguration = new HierarchicalINIConfiguration(file);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(getParent(), new JLabel("Error while loading properties file!"), "Error!", JOptionPane.ERROR_MESSAGE);
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
					selectedSection = section;
					this.propertiesConfiguration = propertiesConfiguration;
					isFirstConfiguration = false;
				}
				propertiesConfigurationSections.put(section.toString(), propertiesConfiguration);
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
				selectedSection = section;
				this.propertiesConfiguration = propertiesConfiguration;
				propertiesConfigurationSections.put(section, propertiesConfiguration);
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

	private JSplitPane createBasicTypesAndOptionsTab() {
		//TODO: Slider neben Zahlenwerten einsetzen
		JSplitPane basicTypesAndOptionsPanel;
		
		JPanel leftUpper = new JPanel();
		leftUpper.setLayout(new BoxLayout(leftUpper,BoxLayout.PAGE_AXIS));
		intConf.getModel().setValueAt("<html><b>Integer</b></html>",0,0);
		intConf.setPreferredScrollableViewportSize(new Dimension(intConf.getWidth(),intConf.getRowHeight()*intConf.getRowCount()));
		intConf.setSelectionBackground(Color.white);
		realConf.getModel().setValueAt("<html><b>Real</b></html>",0,0);
		realConf.setPreferredScrollableViewportSize(new Dimension(realConf.getWidth(),realConf.getRowHeight()*realConf.getRowCount()));
		realConf.setSelectionBackground(Color.white);
		stringConf.getModel().setValueAt("<html><b>String</b></html>",0,0);
		stringConf.setPreferredScrollableViewportSize(new Dimension(stringConf.getWidth(),stringConf.getRowHeight()*stringConf.getRowCount()));
		stringConf.setSelectionBackground(Color.white);
		JScrollPane intScroll = new JScrollPane(intConf);
		JScrollPane realScroll = new JScrollPane(realConf);
		JScrollPane stringScroll = new JScrollPane(stringConf);
		Dimension space = new Dimension(0,10);
		leftUpper.add(intScroll);
		leftUpper.add(Box.createRigidArea(space));
		leftUpper.add(realScroll);
		leftUpper.add(Box.createRigidArea(space));
		leftUpper.add(stringScroll);

		JPanel leftLower = new JPanel(new BorderLayout()); 
		JTextArea abstractClassesText = new JTextArea();
		abstractClassesText.setBackground(this.getBackground());
		abstractClassesText.setText(abstractClassesChildren());
		leftLower.add(abstractClassesText, BorderLayout.CENTER);
		
		JPanel rightUpper = new JPanel(new BorderLayout());
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
		options.getColumn("On").setCellRenderer(new SwitchRenderer());
		options.getColumn("Off").setCellRenderer(new SwitchRenderer());
		options.getColumn("On").setCellEditor(new SwitchEditor(new JCheckBox()));
		options.getColumn("Off").setCellEditor(new SwitchEditor(new JCheckBox()));
		options.setPreferredScrollableViewportSize(new Dimension(350,options.getRowHeight()*options.getRowCount()));
		options.getColumnModel().getColumn(0).setPreferredWidth(200);
		rightUpper.add(new JScrollPane(options), BorderLayout.CENTER);

		JPanel rightLower = new JPanel(new BorderLayout());
		JTextArea legendText = new JTextArea(LegendEntry.LEGEND);
		legendText.setEditable(false);
        legendText.setBackground(getParent().getBackground());
        legendText.setLineWrap(true);
        legendText.setWrapStyleWord(true);
        JScrollPane legenScroll = new JScrollPane(legendText);
		rightLower.add(legenScroll, BorderLayout.CENTER);

		JSplitPane left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftUpper, leftLower);
		JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightUpper, rightLower);
		right.setDividerLocation(OPTIONS_TABLE_HEIGHT);
		right.setDividerSize(OPTIONS_TABLE_DIVIDER_HEIGHT);
	
		basicTypesAndOptionsPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		basicTypesAndOptionsPanel.setDividerLocation(400);
		
		return basicTypesAndOptionsPanel;
	}

	private JSplitPane createClassesAndAssociationsTab() {
		//TODO: Slider neben Zahlenwerten einsetzen
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
	
	private JPanel createInvariantsTab() {
		JScrollPane invariantsScrollPane = new JScrollPane(invariants);
		
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
		
		invariants.getColumn("Active").setCellRenderer(new SwitchRenderer());
		invariants.getColumn("Inactive").setCellRenderer(new SwitchRenderer());
		invariants.getColumn("Negate").setCellRenderer(new SwitchRenderer());
		
		invariants.getColumn("Active").setCellEditor(new SwitchEditor(new JCheckBox()));
		invariants.getColumn("Inactive").setCellEditor(new SwitchEditor(new JCheckBox()));
		invariants.getColumn("Negate").setCellEditor(new SwitchEditor(new JCheckBox()));
		
		invariants.setPreferredScrollableViewportSize(new Dimension(800,invariants.getRowHeight()*invariants.getRowCount()));
		invariants.getColumnModel().getColumn(0).setPreferredWidth(400);
		invariantsScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()));
		
		JPanel invariantsPanel = new JPanel(new BorderLayout());
		invariantsPanel.add(invariantsScrollPane, BorderLayout.CENTER);
		
		return invariantsPanel;
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
	 */
	private void insertConfigurationInBasicTypes() {
		intConf.getModel().setValueAt(propertiesConfiguration.getInt("Integer_min"), 0, 1);
		intConf.getModel().setValueAt(propertiesConfiguration.getInt("Integer_max"), 0, 2);
		if (!(propertiesConfiguration.getProperty("Integer") == null)) {
			intConf.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("Integer")), 0, 3);
		} else intConf.getModel().setValueAt(null, 0, 3);
		realConf.getModel().setValueAt(propertiesConfiguration.getInt("Real_min"), 0, 1);
		realConf.getModel().setValueAt(propertiesConfiguration.getInt("Real_max"), 0, 2);
		if (propertiesConfiguration.containsKey("Real_step")) {
			realConf.getModel().setValueAt(propertiesConfiguration.getDouble("Real_step"), 0, 3);
		}
		if (!(propertiesConfiguration.getProperty("Real") == null)) {
			realConf.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("Real")), 0, 3);
		} else realConf.getModel().setValueAt(null, 0, 3);
		stringConf.getModel().setValueAt(propertiesConfiguration.getInt("String_min"), 0, 1);
		stringConf.getModel().setValueAt(propertiesConfiguration.getInt("String_max"), 0, 2);
		if (!(propertiesConfiguration.getProperty("String") == null)) {
			stringConf.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty("String")), 0, 3);
		} else {
			stringConf.getModel().setValueAt(null, 0, 3);
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
				} else {
					classes.getModel().setValueAt(null, row,1);
				}
				if (propertiesConfiguration.containsKey(className+"_max")) {
					classes.getModel().setValueAt(propertiesConfiguration.getInt(className+"_max"),row,2);
				} else {
					classes.getModel().setValueAt(null, row,2);
				}
				if (propertiesConfiguration.containsKey(className)) {
					if (propertiesConfiguration.getProperty(className) != null) {
						classes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(className)),row,3);
					} else {
						classes.getModel().setValueAt(null, row,3);
					}
				} else {
					classes.getModel().setValueAt(null, row,3);
				}
			} else {
				if (propertiesConfiguration.containsKey(className+"_ac")) {
					if (propertiesConfiguration.getProperty(className) != null) {
						classes.getModel().setValueAt(prepareConfigurationValuesForTable(propertiesConfiguration.getProperty(className+"_ac")),row,3);
					} else {
						classes.getModel().setValueAt(null, row,3);
					}
				} else {
					classes.getModel().setValueAt(null, row,3);
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
				if (!(clazz instanceof MAssociationClass)) {
					if (propertiesConfiguration.containsKey(attributeName+"_minSize")) {
						attributesData[row][3] = propertiesConfiguration.getInt(attributeName+"_minSize");
					} else {
						attributesData[row][3] = null;
					}
				} else {
					attributesData[row][3] = NON_EDITABLE;
				}
				if (!(clazz instanceof MAssociationClass)) {
					if (propertiesConfiguration.containsKey(attributeName+"_maxSize")) {
						attributesData[row][4] = propertiesConfiguration.getInt(attributeName+"_maxSize");
					} else {
						attributesData[row][4] = null;
					}
				} else {
					attributesData[row][4] = NON_EDITABLE;
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
		}
	}
	
	/**
	 * updates the attributes table with changed values found in the chosen properties configuration
	 * @param className
	 */
	private void updateClassAttributes(String className) {
		AttributeTableModel tableModel = (AttributeTableModel)this.attributes.getModel();
		tableModel.setClass(this.config.getClassSettings(className));
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
				((JRadioButton) options.getModel().getValueAt(0,2)).setSelected(true);
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
				((JRadioButton) options.getModel().getValueAt(1,1)).setSelected(true);
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
				((JRadioButton) invariants.getModel().getValueAt(i, 1)).setSelected(true);
			}
		}
		options.repaint();
		invariants.repaint();
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
	
	private void saveConfigurationsToFile() {
		try {
			PropertiesWriter.writeToFile(propertiesConfigurationSections, file, model);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(getParent(), new JLabel("Error while saving configuration to file!"), "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// this should do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Component component = e.getComponent();
		if ((component instanceof JTable) && component.getName() != null) {
			JTable table = (JTable) component;
			int column = table.columnAtPoint(e.getPoint());
			switch (table.getName()) {
			case ("intConf"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.INTEGER_MIN): {
					explainArea.setText(LegendEntry.INT_MINIMUM);
					break;
				}
				case (ConfigurationConversion.INTEGER_MAX): {
					explainArea.setText(LegendEntry.INT_MAXIMUM);
					break;
				}
				case (ConfigurationConversion.INTEGER_VALUES): {
					explainArea.setText(LegendEntry.INT_VALUES);
					break;
				}
				}
				break;
			}
			case ("realConf"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.REAL_MIN): {
					explainArea.setText(LegendEntry.REAL_MINIMUM);
					break;
				}
				case (ConfigurationConversion.REAL_MAX): {
					explainArea.setText(LegendEntry.REAL_MAXIMUM);
					break;
				}
				case (ConfigurationConversion.REAL_STEP): {
					explainArea.setText(LegendEntry.REAL_STEP);
					break;
				}
				case (ConfigurationConversion.REAL_VALUES): {
					explainArea.setText(LegendEntry.REAL_VALUES);
					break;
				}
				}
				break;
			}
			case ("stringConf"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.STRING_MIN): {
					explainArea.setText(LegendEntry.STRING_MINPRESENT);
					break;
				}
				case (ConfigurationConversion.STRING_MAX): {
					explainArea.setText(LegendEntry.STRING_MAXPRESENT);
					break;
				}
				case (ConfigurationConversion.STRING_VALUES): {
					explainArea.setText(LegendEntry.STRING_PRESENTSTRINGS);
					break;
				}
				}
				break;
			}
			case ("classes"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.CLASSES_MIN): {
					explainArea.setText(LegendEntry.CLASS_MININSTANCES);
					break;
				}
				case (ConfigurationConversion.CLASSES_MAX): {
					explainArea.setText(LegendEntry.CLASS_MAXINSTANCES);
					break;
				}
				case (ConfigurationConversion.CLASSES_VALUES): {
					explainArea.setText(LegendEntry.CLASS_INSTANCENAMES);
					break;
				}
				}
				break;
			}
			case ("attributes"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.ATTRIBUTES_MIN): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MINDEFINED);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAX): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MAXDEFINED);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MINSIZE): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MINELEMENTS);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAXSIZE): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MAXELEMENTS);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_VALUES): {
					explainArea.setText(LegendEntry.ATTRIBUTES_ATTRIBUTEVALUES);
					break;
				}
				}
				break;
			}
			case ("associations"): {
				switch (table.getColumnName(column)) {
				case (ConfigurationConversion.ASSOCIATIONS_MIN): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_MINLINKS);
					break;
				}
				case (ConfigurationConversion.ASSOCIATIONS_MAX): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_MAXLINKS);
					break;
				}
				case (ConfigurationConversion.ASSOCIATIONS_VALUES): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_PRESENTLINKS);
					break;
				}
				}
				break;
			}
			}

		} else if (component instanceof JTableHeader) {
			JTableHeader tableheader = (JTableHeader) component;
			int column = tableheader.columnAtPoint(e.getPoint());
			String tableName = tableheader.getTable().getName();
			String columnName = tableheader.getTable().getColumnName(column);
			switch (tableName) {
			case ("intConf"): {
				switch (columnName) {
				case (ConfigurationConversion.INTEGER_MIN): {
					explainArea.setText(LegendEntry.INT_MINIMUM);
					break;
				}
				case (ConfigurationConversion.INTEGER_MAX): {
					explainArea.setText(LegendEntry.INT_MAXIMUM);
					break;
				}
				case (ConfigurationConversion.INTEGER_VALUES): {
					explainArea.setText(LegendEntry.INT_VALUES);
					break;
				}
				}
				break;
			}
			case ("realConf"): {
				switch (columnName) {
				case (ConfigurationConversion.REAL_MIN): {
					explainArea.setText(LegendEntry.REAL_MINIMUM);
					break;
				}
				case (ConfigurationConversion.REAL_MAX): {
					explainArea.setText(LegendEntry.REAL_MAXIMUM);
					break;
				}
				case (ConfigurationConversion.REAL_STEP): {
					explainArea.setText(LegendEntry.REAL_STEP);
					break;
				}
				case (ConfigurationConversion.REAL_VALUES): {
					explainArea.setText(LegendEntry.REAL_VALUES);
					break;
				}
				}
				break;
			}
			case ("stringConf"): {
				switch (columnName) {
				case (ConfigurationConversion.STRING_MIN): {
					explainArea.setText(LegendEntry.STRING_MINPRESENT);
					break;
				}
				case (ConfigurationConversion.STRING_MAX): {
					explainArea.setText(LegendEntry.STRING_MAXPRESENT);
					break;
				}
				case (ConfigurationConversion.STRING_VALUES): {
					explainArea.setText(LegendEntry.STRING_PRESENTSTRINGS);
					break;
				}
				}
				break;
			}
			case ("classes"): {
				switch (columnName) {
				case (ConfigurationConversion.CLASSES_MIN): {
					explainArea.setText(LegendEntry.CLASS_MININSTANCES);
					break;
				}
				case (ConfigurationConversion.CLASSES_MAX): {
					explainArea.setText(LegendEntry.CLASS_MAXINSTANCES);
					break;
				}
				case (ConfigurationConversion.CLASSES_VALUES): {
					explainArea.setText(LegendEntry.CLASS_INSTANCENAMES);
					break;
				}
				}
				break;
			}
			case ("attributes"): {
				switch (columnName) {
				case (ConfigurationConversion.ATTRIBUTES_MIN): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MINDEFINED);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAX): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MAXDEFINED);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MINSIZE): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MINELEMENTS);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAXSIZE): {
					explainArea.setText(LegendEntry.ATTRIBUTES_MAXELEMENTS);
					break;
				}
				case (ConfigurationConversion.ATTRIBUTES_VALUES): {
					explainArea.setText(LegendEntry.ATTRIBUTES_ATTRIBUTEVALUES);
					break;
				}
				}
				break;
			}
			case ("associations"): {
				switch (columnName) {
				case (ConfigurationConversion.ASSOCIATIONS_MIN): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_MINLINKS);
					break;
				}
				case (ConfigurationConversion.ASSOCIATIONS_MAX): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_MAXLINKS);
					break;
				}
				case (ConfigurationConversion.ASSOCIATIONS_VALUES): {
					explainArea.setText(LegendEntry.ASSOCIATIONS_PRESENTLINKS);
					break;
				}
				}
				break;
			}
			}
		} else {
			explainArea.setText("");
		}
	}
	
	private Set<MClass> getAbstractClasses(MModel model) {
		Iterator<MClass> classIterator = model.classes().iterator();
		Set<MClass> abstractClasses = new HashSet<MClass>();
		while (classIterator.hasNext()) {
			MClass clazz = classIterator.next();
			if (clazz.isAbstract()) {
				abstractClasses.add(clazz);
			}
		}
		return abstractClasses;
	}
	
	private String abstractClassesChildren() {
		/*
		 * "Abstract Classes: \n Base_Named \n\n"
						+ "Inheriting Classes:\n"
						+ "Base_Named > Base_DataType \n"
						+ "Base_Named > Base_Attribute \n"
						+ "Base_Named > ErSyn_ErSchema \n"
						+ "Base_Named > ErSyn_Entity \n"
						+ "Base_Named > ErSyn_Relship \n"
						+ "Base_Named > ErSyn_Relend \n"
						+ "Base_Named > RelSyn_RelDBSchema \n"
						+ "Base_Named > RelSyn_RelSchema");
		 */
		String abstractText = "Abstract Classes: \n";
		Iterator<MClass> abstractClasses = getAbstractClasses(model).iterator();
		while (abstractClasses.hasNext()) {
			MClass abstractClass = abstractClasses.next();
			abstractText += abstractClass.name();
			if (abstractClasses.hasNext()) {
				abstractText += ", ";
			} else {
				abstractText += "\n\n";
			}
		}
		abstractClasses = getAbstractClasses(model).iterator();
		if (abstractClasses.hasNext()) {
			abstractText += "Inheriting Classes:\n";
		}
		while (abstractClasses.hasNext()) {
			MClass abstractClass = abstractClasses.next();
			Set<? extends MClass> inheritingClasses = abstractClass.allChildren();
			Iterator<? extends MClass> inheritingClassesIterator = inheritingClasses.iterator();
			while (inheritingClassesIterator.hasNext()) {
				abstractText += abstractClass.name()+" > "+inheritingClassesIterator.next().name()+"\n";
			}
		}
		
		return abstractText;
	}
	
}
