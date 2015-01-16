package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.kodkod.plugin.PropertiesWriter;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.util.ConfigurationChange;
import org.tzi.use.kodkod.plugin.gui.util.StringChange;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationClassImpl;
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
	
	private static final String ASSOCIATIONCLASS_INDICATOR = " [AC]";
	//TODO: Alle nicht editierbaren Tabellenzellen ausgrauen, statt nur "non-editable" zu schreiben
	private static final String NON_EDITABLE = "non-editable";
	private static final int OPTIONS_TABLE_DIVIDER_HEIGHT = 2;
	private static final int OPTIONS_TABLE_HEIGHT = 64;
	
	int defaultNameCount = 0;
	String selectedSection;
	String selectedButton;
	JLabel currentFileLabel;
	JTextArea statusArea;
	
	private JTable invariants;
	private JTable options;
	private JTable associations;
	private JTable attributes;
	private JTable classes;
	private JTable integer;
	private JTable real;
	private JTable string;
	private Set<JTable> tables;

	private MModel model;
	private File file;
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurationSections;
	private PropertiesConfiguration propertiesConfiguration;
	private SettingsConfiguration settingsConfiguration;
	private TableBuilder tableBuilder;

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
				propertiesConfiguration = ConfigurationChange.toProperties(settingsConfiguration, model);
				propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
			}
			selectedSection = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
			propertiesConfiguration = (PropertiesConfiguration) propertiesConfigurationSections.get(selectedSection).clone();
			settingsConfiguration = ConfigurationChange.toSettings(model, propertiesConfiguration, settingsConfiguration);
			TableBuilder.repaintAllTables(tables.iterator());
			configurationChanged = false;
			classes.repaint();
			classes.clearSelection();
			System.out.println("Configuration loaded.");
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
	            updateClassAssociations(selectedClass);
	        }
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
		//TODO: Probieren, ob this.setUndecorated hier geimplementiert werden kann, um die windows nativen Minimize/Maximize Buttons
		//zu bekommen
		//this.setUndecorated

		this.model = model;
		settingsConfiguration = new SettingsConfiguration(model);
		tableBuilder = new TableBuilder(settingsConfiguration);
		
		//TODO: Hier kommen vielleicht noch Benachrichtigungen aus dem Validierungsprozess herein
		statusArea = new JTextArea();
		
		//building all tables and adding them to a HashSet for repainting alltogether purposes
		tables = new HashSet<JTable>();
		integer = tableBuilder.integer();
		real = tableBuilder.real();
		string = tableBuilder.string();
		options = tableBuilder.options();
		classes = tableBuilder.classes();
		attributes = tableBuilder.attributes();
		associations = tableBuilder.associations();
		invariants = tableBuilder.invariants();
		tables.add(integer);
		tables.add(real);
		tables.add(string);
		tables.add(options);
		tables.add(classes);
		tables.add(attributes);
		tables.add(associations);
		tables.add(invariants);

		classTableSelectionListener = classes.getSelectionModel();
		classTableSelectionListener.addListSelectionListener(new ClassTableSelectionHandler());
		classes.setSelectionModel(classTableSelectionListener);
		
		file = new File(model.filename().replaceAll("\\.use", "") + ".properties");
		currentFileLabel = new JLabel(file.getAbsolutePath());
		propertiesConfigurationSections = new Hashtable<String, PropertiesConfiguration>();
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
        			extractConfigurations(file);
        			settingsConfiguration = ConfigurationChange.toSettings(model, propertiesConfiguration, settingsConfiguration);
        			TableBuilder.repaintAllTables(tables.iterator());
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
        		propertiesConfiguration = ConfigurationChange.toProperties(settingsConfiguration, model);
        		propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
        		saveConfigurationsToFile();
        		configurationChanged = false;
        	}
        });
        
        renameConfigurationButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		String newName = JOptionPane.showInputDialog("Please input the new name of this configuration:", selectedSection);
        		propertiesConfiguration = ConfigurationChange.toProperties(settingsConfiguration, model);
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
        
        //TODO: "Speichern unter..."-Button und -Funktion machen
        
        //TODO: Warnung ausgeben dass, falls die Konfiguration nicht abgespeichert(Save-Button gedrueckt) wurde,
        //diese in der folgenden Validierung zwar verwendet wird, danach aber verworfen ist.
        //Das hier entfernen, wenn die Validierung in einem Thread mit der GUI ausgefuert wird.
        validateButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		propertiesConfiguration = ConfigurationChange.toProperties(settingsConfiguration, model);
        		validatable = true;
        		setVisible(false);
        	}
        } );

        statusArea.setEditable(false);
        statusArea.setBackground(getParent().getBackground());
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setText("Test Jo");
        
        //TODO: Die Buttons "Open", "Save", "New configuration", "Rename", "New", "Delete",(spaeter auch: "Save as", "New file")
        //sollen als zu Menu-Punkten umgewandelt werden, statt der jetzigen Toolbar-Form
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
        center.add("Basic types", buildBasicTypesAndOptionsTab());
        center.add("Classes and associations", buildClassesAndAssociationsTab());
        center.add("Invariants and options", buildInvariantsTab());
        southWest.add(validateButton);
        southCenter.add(statusArea, BorderLayout.CENTER);
        south.add(southWest, BorderLayout.WEST);
        south.add(southCenter, BorderLayout.CENTER);
        main.setLayout(new BorderLayout());
        main.add(north, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER); 
        main.add(south, BorderLayout.SOUTH);
        
        extractConfigurations(file);
        settingsConfiguration = ConfigurationChange.toSettings(model, propertiesConfiguration, settingsConfiguration);
        TableBuilder.repaintAllTables(tables.iterator());
        configurationChanged = false;
        /* TODO: Entfernen oder andere Methode finden, um die Tabellen mit aus der
         * Konfiguration extrahierten Daten zu fuellen
        insertConfigurationInBasicTypes();
        insertConfigurationInClasses();
        insertConfigurationInAttributes();
        insertConfigurationInAssociations();
        insertConfigurationInInvariantsOptions();*/
        classes.setRowSelectionInterval(0,0);
        

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
	private void extractConfigurations(File file) {
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
			Iterator<?> sectionsIterator = hierarchicalINIConfiguration.getSections().iterator();
			Boolean isFirstConfiguration = true;
			while (sectionsIterator.hasNext()) {
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
				String section = (String) sectionsIterator.next();
				SubnodeConfiguration sectionConfigurations = hierarchicalINIConfiguration.getSection(section);
				Iterator<?> keysIterator = sectionConfigurations.getKeys();
				while (keysIterator.hasNext()) {
					String key = (String) keysIterator.next().toString();
					if (!key.startsWith("--")){
						String value = sectionConfigurations.getString(key);
						propertiesConfiguration.addProperty(key, value);
					}
				}
				if (isFirstConfiguration) {
					selectedSection = section;
					this.propertiesConfiguration = propertiesConfiguration;
					isFirstConfiguration = false;
				}
				propertiesConfigurationSections.put(section.toString(), propertiesConfiguration);
				sectionSelectionComboBox.addItem(section.toString());
			}
		} else if (!hierarchicalINIConfiguration.getKeys().hasNext()) {
			JOptionPane.showMessageDialog(getParent(), new JLabel("Not a proper .properties File! Choose another or delete it!"), "Error!", JOptionPane.ERROR_MESSAGE);
		} else {
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
				String section = "default";
				Iterator<?> keysIterator = hierarchicalINIConfiguration.getKeys();
				while (keysIterator.hasNext()) {
					String key = (String) keysIterator.next();
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

	private JSplitPane buildBasicTypesAndOptionsTab() {
		//TODO: Slider neben Zahlenwerten einsetzen
		JSplitPane basicTypesAndOptionsPanel;
		
		JPanel leftUpper = new JPanel();
		leftUpper.setLayout(new BoxLayout(leftUpper,BoxLayout.PAGE_AXIS));
		
		JScrollPane intScroll = new JScrollPane(integer);
		JScrollPane realScroll = new JScrollPane(real);
		JScrollPane stringScroll = new JScrollPane(string);
		Dimension space = new Dimension(0,10);
		leftUpper.add(intScroll);
		leftUpper.add(Box.createRigidArea(space));
		leftUpper.add(realScroll);
		leftUpper.add(Box.createRigidArea(space));
		leftUpper.add(stringScroll);

		JPanel leftLower = new JPanel(new BorderLayout()); 
		JTextArea abstractClassesText = new JTextArea();
		abstractClassesText.setBackground(this.getBackground());
		abstractClassesText.setText(abstractClassesChildren(this.model));
		leftLower.add(abstractClassesText, BorderLayout.CENTER);
		
		JPanel rightUpper = new JPanel(new BorderLayout());
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

	private JSplitPane buildClassesAndAssociationsTab() {
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
	
	private JPanel buildInvariantsTab() {
		JScrollPane invariantsScrollPane = new JScrollPane(invariants);
		invariantsScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()));
		JPanel invariantsPanel = new JPanel(new BorderLayout());
		invariantsPanel.add(invariantsScrollPane, BorderLayout.CENTER);
		
		return invariantsPanel;
	}
	
	/**
	 * fills gui table for basic types with the values from the chosen propertiesConfiguration
	 */
	private void insertConfigurationInBasicTypes() {
		integer.getModel().setValueAt(propertiesConfiguration.getInt("Integer_min"), 0, 1);
		integer.getModel().setValueAt(propertiesConfiguration.getInt("Integer_max"), 0, 2);
		if (!(propertiesConfiguration.getProperty("Integer") == null)) {
			integer.getModel().setValueAt(StringChange.prepareForTable(propertiesConfiguration.getProperty("Integer")), 0, 3);
		} else integer.getModel().setValueAt(null, 0, 3);
		real.getModel().setValueAt(propertiesConfiguration.getInt("Real_min"), 0, 1);
		real.getModel().setValueAt(propertiesConfiguration.getInt("Real_max"), 0, 2);
		if (propertiesConfiguration.containsKey("Real_step")) {
			real.getModel().setValueAt(propertiesConfiguration.getDouble("Real_step"), 0, 3);
		}
		if (!(propertiesConfiguration.getProperty("Real") == null)) {
			real.getModel().setValueAt(StringChange.prepareForTable(propertiesConfiguration.getProperty("Real")), 0, 3);
		} else real.getModel().setValueAt(null, 0, 3);
		string.getModel().setValueAt(propertiesConfiguration.getInt("String_min"), 0, 1);
		string.getModel().setValueAt(propertiesConfiguration.getInt("String_max"), 0, 2);
		if (!(propertiesConfiguration.getProperty("String") == null)) {
			string.getModel().setValueAt(StringChange.prepareForTable(propertiesConfiguration.getProperty("String")), 0, 3);
		} else {
			string.getModel().setValueAt(null, 0, 3);
		}
	}
	
	private Boolean isAssociationclass(MClass clazz) {
		if (clazz.getClass().equals(MAssociationClassImpl.class)) {
			return true;
		}
		return false;
	}
	
	private Boolean isAssociationclass(MAssociation association) {
		//FIXME: Probieren, ob auch (association instanceof MAssociationClassImpl)
		//als Bedingung geht ODER ob nicht auch (model.associations().contains(clazz))
		//noch besser waere
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
					classes.getModel().setValueAt( StringChange.html(StringChange.italic(className+StringChange.bold(ASSOCIATIONCLASS_INDICATOR))) ,row,0);
				} else {
					classes.getModel().setValueAt( StringChange.html(className+StringChange.italic(StringChange.bold(ASSOCIATIONCLASS_INDICATOR))) ,row,0);
					classes.getModel().setValueAt(NON_EDITABLE,row,1);
					classes.getModel().setValueAt(NON_EDITABLE,row,2);
				}
			} else {
				if (clazz.isAbstract()) {
					classes.getModel().setValueAt( StringChange.html(StringChange.italic(className)) ,row,0);
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
						classes.getModel().setValueAt(StringChange.prepareForTable(propertiesConfiguration.getProperty(className)),row,3);
					} else {
						classes.getModel().setValueAt(null, row,3);
					}
				} else {
					classes.getModel().setValueAt(null, row,3);
				}
			} else {
				if (propertiesConfiguration.containsKey(className+"_ac")) {
					if (propertiesConfiguration.getProperty(className) != null) {
						classes.getModel().setValueAt(StringChange.prepareForTable(propertiesConfiguration.getProperty(className+"_ac")),row,3);
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
						attributesData[row][5] = StringChange.prepareForTable(propertiesConfiguration.getProperty(attributeName));
					} else {
						attributesData[row][5] = null;
					}
				}
				row++;
			}
		}
	}
	
	/**
	 * updates the attributes table refering to given className and gets them from the SettingsConfiguration
	 * @param className
	 */
	private void updateClassAttributes(String className) {
		TableModelAttribute attributeModel = (TableModelAttribute)this.attributes.getModel();
		attributeModel.setClass(this.settingsConfiguration.getClassSettings(className));
	}
	
	private void updateClassAssociations(String className) {
		TableModelAssociation associationModel = (TableModelAssociation)this.associations.getModel();
		associationModel.setClass(this.settingsConfiguration.getClassSettings(className));
	}
	
	/**
	 * fills the hashtable classAssociations with associations referenced to their
	 * first association end
	 * @param pc
	 * @param model
	 */
	//FIXME: Aendern oder entfernen, wenn spaeter Tabelle auch ohne das hier geht
	private void insertConfigurationInAssociations() {
		Iterator<MClass> classes = model.classes().iterator();

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
						associationsData[row][0] = StringChange.html(StringChange.italic(associationName+StringChange.bold(ASSOCIATIONCLASS_INDICATOR)));
					} else if (isAssociationclass(association) && !association.isAbstract()) {
						associationsData[row][0] = StringChange.html(associationName+StringChange.italic(StringChange.bold(ASSOCIATIONCLASS_INDICATOR)));
					} else if (!isAssociationclass(association) && association.isAbstract()) {
						associationsData[row][0] = StringChange.html(StringChange.italic(associationName));
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
							associationsData[row][3] = StringChange.prepareForTable(propertiesConfiguration.getProperty(associationName));
						} else {
							associationsData[row][3] = null;
						}
					}
					row++;
				}
			}
		}
	}
	
	// FIXME: Aendern oder entfernen, wenn Tabelle nicht mehr benoetigt
	private void insertConfigurationInInvariantsOptions() {
		if ((propertiesConfiguration.containsKey(PropertyEntry.aggregationcyclefreeness)) && (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness) != null)) {
			if (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness).equalsIgnoreCase("on")) {
				options.getModel().setValueAt(true,0,1);
			} else if (propertiesConfiguration.getString(PropertyEntry.aggregationcyclefreeness).equalsIgnoreCase("off")) {
				options.getModel().setValueAt(false,0,1);
			} else {
				options.getModel().setValueAt(false,0,1);
			}
		} else {
			options.getModel().setValueAt(false,0,1);
		}
		if ((propertiesConfiguration.containsKey(PropertyEntry.forbiddensharing)) && (propertiesConfiguration.getString(PropertyEntry.forbiddensharing) != null)) {
			if (propertiesConfiguration.getString(PropertyEntry.forbiddensharing).equalsIgnoreCase("on")) {
				options.getModel().setValueAt(true,1,1);
			} else if (propertiesConfiguration.getString(PropertyEntry.forbiddensharing).equalsIgnoreCase("off")) {
				options.getModel().setValueAt(false,1,1);
			} else {
				options.getModel().setValueAt(true,1,1);
			}
		} else {
			options.getModel().setValueAt(true,1,1);
		}
		
		int invCount = model.classInvariants().size();
		for (int i = 0; i < invCount; i++) {
			String invNameOfRow = (String) invariants.getModel().getValueAt(i, 0);
			if ((propertiesConfiguration.containsKey(invNameOfRow)) && (propertiesConfiguration.getString(invNameOfRow) != null) ) {
				if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("active")) {
					invariants.getModel().setValueAt(true, i, 1);
					invariants.getModel().setValueAt(false, i, 2);
				} else if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("inactive")) {
					invariants.getModel().setValueAt(false, i, 1);
					invariants.getModel().setValueAt(false, i, 2);
				} else if (propertiesConfiguration.getString(invNameOfRow).equalsIgnoreCase("negate")) {
					invariants.getModel().setValueAt(true, i, 1);
					invariants.getModel().setValueAt(true, i, 2);
				} else {
					//TODO: Fehlermeldung hier rausbringen
					System.out.println("Wrong value for "+invNameOfRow+"; it must be \"active\", \"inactive\" or \"negate\".");
				}
			} else {
				invariants.getModel().setValueAt(true, i, 1);
				invariants.getModel().setValueAt(false, i, 2);
			}
		}
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
	
	private String abstractClassesChildren(MModel model) {
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
