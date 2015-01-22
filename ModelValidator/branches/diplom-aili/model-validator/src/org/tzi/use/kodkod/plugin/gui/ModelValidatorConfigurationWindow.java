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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.kodkod.plugin.PropertiesWriter;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.util.ChangeConfiguration;
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
	private Boolean configurationChanged; //TODO: checken, wo es wie gesetzt werden kann, um sinnvoll zu sein
	
	private JTabbedPane center;
	private JPanel main;
	private FlowLayout leftFlowLayout;
	private JPanel northNorth;
	private JPanel northSouth;
	private JPanel north;
	private JPanel southWest;
	private JPanel southCenter;
	private JPanel south;
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
		//FIXME: Hier oder irgendwo anders werden die Settings nicht ordentlich gespeichert, bevor die
		//Konfiguration gewechselt wird
		@Override
		public void actionPerformed(ActionEvent e) {
			if (propertiesConfiguration != null) {
				propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
				propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
			}
			selectedSection = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
			propertiesConfiguration = (PropertiesConfiguration) propertiesConfigurationSections.get(selectedSection).clone();
			settingsConfiguration = ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
			TableBuilder.repaintAllTables(tables.iterator());
			configurationChanged = false;
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
	            selectedClass = (String) classes.getValueAt(selectedRow, 0);
	            updateClassAttributes(selectedClass);
	            updateClassAssociations(selectedClass);
	        }
        }
    }
	
	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		//this.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//TODO: USE soll weiterhin verwendbar bleiben, waehrrend die MV-GUI weiterlaeuft. Dies soll geschehen, indem die Validierung
		// im Thread der MV-GUI mit ausgefuehrt wird.
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(true);
		this.setSize(800,300);
		//TODO: Probieren, ob this.setUndecorated hier geimplementiert werden kann, um die windows nativen Minimize/Maximize Buttons
		//zu bekommen
		//this.setUndecorated(true);

		this.model = model;
		settingsConfiguration = new SettingsConfiguration(model);
		tableBuilder = new TableBuilder(settingsConfiguration);
		
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
        leftFlowLayout = new FlowLayout(FlowLayout.LEFT);
        northNorth = new JPanel(leftFlowLayout);
        northSouth = new JPanel(leftFlowLayout);
        north = new JPanel(new BorderLayout());
        southWest = new JPanel(leftFlowLayout);
        southCenter = new JPanel(new BorderLayout());
        southCenter.setBorder(new BevelBorder(BevelBorder.LOWERED));
        south = new JPanel(new BorderLayout());

        sectionSelectionComboBox = new JComboBox<String>();

        validateButton = new JButton("Validate");

        comboBoxActionListener = new ComboBoxActionListener();  
        sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        
       validateButton.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
        		validatable = true;
        		setVisible(false);
        	}
        } );

        statusArea.setEditable(false);
        statusArea.setBackground(getParent().getBackground());
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        //TODO: Hier kommen spaeter die Benachrichtigungen aus dem Validierungsprozess herein
        statusArea.setText("");
        
        northNorth.add(new JLabel("Loaded properties file: "));
        northNorth.add(currentFileLabel);
        northSouth.add(new JLabel("Loaded configuration: "));
        northSouth.add(sectionSelectionComboBox);
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
        
        //TODO wenn keine standard properties-File vorhanden ist, soll auch keine erstellt werden
        extractConfigurations(file);
        settingsConfiguration = ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
        TableBuilder.repaintAllTables(tables.iterator());
        configurationChanged = false;
        classes.setRowSelectionInterval(0,0);
        
        this.setJMenuBar(buildMenuBar(parent));
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
		HierarchicalINIConfiguration hierarchicalINIConfiguration;
		try {
			hierarchicalINIConfiguration = new HierarchicalINIConfiguration(file);
		} catch (ConfigurationException e) {
			//e.printStackTrace();
			//JOptionPane.showMessageDialog(getParent(), new JLabel("Error while loading properties file!"), "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
		sectionSelectionComboBox.removeAllItems();
		propertiesConfigurationSections = new Hashtable<String, PropertiesConfiguration>();
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
	
	private JMenuBar buildMenuBar(final JFrame parent) {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem saveAsMenuItem = new JMenuItem("Save as..."); //TODO
		JMenu configurationMenu = new JMenu("Configuration");
		JMenuItem renameMenuItem = new JMenuItem("Rename");
		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		JMenuItem newMenuItem = new JMenuItem("New"); //TODO
		JMenuItem cloneMenuItem = new JMenuItem("Clone");
		JMenuItem validateMenuItem = new JMenuItem("Validate"); //TODO
		
		openMenuItem.addActionListener( new ActionListener() {
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
        			settingsConfiguration = ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
        			TableBuilder.repaintAllTables(tables.iterator());
        			configurationChanged = false;
        			classes.clearSelection();
        			currentFileLabel.setText(file.getAbsolutePath());
        		} else {
        			JOptionPane.showMessageDialog(getParent(), new JLabel("Error while loading properties file!"), "Error!", JOptionPane.ERROR_MESSAGE);
        		}
        		
        	};
		});
		
		renameMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		String newName = JOptionPane.showInputDialog("Please input the new name of this configuration:", selectedSection);
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
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
		
		saveMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
        		propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
        		saveConfigurationsToFile();
        		configurationChanged = false;
        	}
        });
		
		renameMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		String newName = JOptionPane.showInputDialog("Please input the new name of this configuration:", selectedSection);
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
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
		
		deleteMenuItem.addActionListener(new ActionListener() {
        	//TODO: Falls die letzte section geloescht wird, einfach eine default sektion mit default-Werten(quasi new)
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
		
		cloneMenuItem.addActionListener(new ActionListener() {
        	//TODO: dieser Button klont nur. einen zusaetzlichen Button, der 
        	//default Wert in der neuen Konfiguration erzeugt, diesen dann "new" nennen und das hier "clone"
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
		validateMenuItem.addActionListener( new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
        		validatable = true;
        		setVisible(false);
        	}
        } );
		
		
		
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		configurationMenu.add(renameMenuItem);
		configurationMenu.add(deleteMenuItem);
		configurationMenu.add(cloneMenuItem);
		configurationMenu.add(newMenuItem);
		configurationMenu.add(validateMenuItem);
		menuBar.add(fileMenu);
		menuBar.add(configurationMenu);
		
		return menuBar;
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
