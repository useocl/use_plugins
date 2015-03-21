package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.table.TableColumn;

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
 *  A GUI for adjusting the configurations before giving them to the model validator
 * 
 * @author Subi Aili
 *
 */
public class ModelValidatorConfigurationWindow extends JDialog {
	

	private static final long serialVersionUID = 1L;
	
	private static final int OPTIONS_TABLE_DIVIDER_HEIGHT = 2;
	private static final int OPTIONS_TABLE_HEIGHT = 64;
	private static final String DEFAULT_CONFIG_NAME = "config";
	
	int defaultNameCount = 0;
	String selectedSection;
	JLabel currentFileLabel;
	JLabel attributesLabel;
	JLabel associationsLabel;
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
	private JCheckBox attributeCheckBox;

	private MModel model;
	private File file;
	private Hashtable<String,PropertiesConfiguration> propertiesConfigurationSections;
	private PropertiesConfiguration propertiesConfiguration;
	private SettingsConfiguration settingsConfiguration;
	private TableBuilder tableBuilder;
	private List<TableColumn> attributeColumnsToHide;

	private String selectedClass;
	private Boolean readyToValidate;
	
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
	private ActionListener validateActionListener;

	/*
	 * Listens for changed selection in the drop down menu, puts previous propertiesConfiguration
	 * into propertiesConfigurationSection with former selectedSection, then gets the current
	 * selected Section and get its propertiesConfiguration, eventually puts the loaded configurations
	 * into settingsConfiguration.
	 */
	private class ComboBoxActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (propertiesConfiguration != null) {
				propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
				propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
			}
			selectedSection = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
			propertiesConfiguration = (PropertiesConfiguration) propertiesConfigurationSections.get(selectedSection).clone();
			//settingsConfiguration are switched when the configuration is exchanged via the Configuration ComboBox. The information
			//if it's actually changed are lost after the switch, thus it's saved here before the switching and is recovered after.
			boolean beforeChange = settingsConfiguration.isChanged();
			ChangeConfiguration.clearSettings(settingsConfiguration);
			ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
			settingsConfiguration.setChanged(beforeChange);
			TableBuilder.repaintAllTables(tables.iterator());
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
	            selectedClass = (String) classes.getValueAt(selectedRow, 0);
	            attributesLabel.setText(ConfigurationTerms.ATTRIBUTES+" of "+selectedClass);
	            associationsLabel.setText(ConfigurationTerms.ASSOCIATIONS+" of "+selectedClass);
	            updateClassAttributes(selectedClass);
	            updateClassAssociations(selectedClass);
	        }
        }
    }
	
	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(true);
		this.setSize(1024,300);
		this.model = model;

		settingsConfiguration = new SettingsConfiguration(model);
		tableBuilder = new TableBuilder(settingsConfiguration);
		
		tables = new HashSet<JTable>(); //building all tables and adding them to a HashSet for repainting alltogether purposes
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
		if (file.exists()) {
			currentFileLabel = new JLabel(file.getAbsolutePath());
		} else {
			currentFileLabel = new JLabel("");
		}
		propertiesConfigurationSections = new Hashtable<String, PropertiesConfiguration>();
		readyToValidate = false;
    	
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
        comboBoxActionListener = new ComboBoxActionListener();  
        sectionSelectionComboBox.addActionListener(comboBoxActionListener);

        attributeColumnsToHide = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
        	attributeColumnsToHide.add( attributes.getColumnModel().getColumn(i));
        }
        
        attributeCheckBox = new JCheckBox("Hide specific bounds", true);
		attributeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if (!attributeCheckBox.isSelected()) {
					if (attributes.getColumnCount() < 6) {
						TableColumn tempColumn = attributes.getColumnModel().getColumn(1);
						attributes.removeColumn(tempColumn);
						for (int i = 0; i < 4; i++) {
							attributes.addColumn(attributeColumnsToHide.get(i));
						}
						attributes.addColumn(tempColumn);
					}
				} else {
					if (attributes.getColumnCount() > 2) {
						for (int i = 0; i < 4; i++) {
							attributes.removeColumn(attributeColumnsToHide.get(i));
						}
					}
				}
        	}
		});

		validateActionListener = new ActionListener() {
        	@Override 
        	public void actionPerformed( ActionEvent e ) {
        		if (settingsConfiguration.isChanged()) {
        			int result = JOptionPane.showConfirmDialog(parent, 
        					"Do you want to save them before Validation?", 
        					"Configurations are not saved yet!", 
        					JOptionPane.YES_NO_CANCEL_OPTION);
    				if (result == 2) {
    					return;
    				} else if (result == 0) {
    					saveConfigurationsToFile(file);
    				}
        		}
        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
        		readyToValidate = true;
        		setVisible(false);
        	}
        };
        
        if (file.exists()) {
        	extractConfigurations(file);
        	ChangeConfiguration.clearSettings(settingsConfiguration);
        	ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
        } else {
        	setAllDefault();
        }
        TableBuilder.repaintAllTables(tables.iterator());
        
        if (file.exists()) {
        	settingsConfiguration.setChanged(false);
        } else {
        	settingsConfiguration.setChanged(true);
        }
		
        validateButton = new JButton("Validate");
        validateButton.addActionListener(validateActionListener);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setBackground(getParent().getBackground());
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setText("");
        
        northNorth.add(new JLabel("Loaded properties file: "));
        northNorth.add(currentFileLabel);
        northSouth.add(new JLabel("Loaded configuration: "));
        northSouth.add(sectionSelectionComboBox);
        north.add(northNorth, BorderLayout.NORTH);
        north.add(northSouth, BorderLayout.SOUTH);
        center.add("Basic Types and Options", buildBasicTypesAndOptionsTab());
        center.add("Classes and Associations", buildClassesAndAssociationsTab());
        center.add("Invariants", buildInvariantsTab());
        southWest.add(validateButton);
        southCenter.add(statusArea, BorderLayout.CENTER);
        south.add(southWest, BorderLayout.WEST);
        south.add(southCenter, BorderLayout.CENTER);
        main.setLayout(new BorderLayout());
        main.add(north, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER); 
        main.add(south, BorderLayout.SOUTH);

        //Hiding the min-/maxDefined and min-/maxElements of the attributes table
        for (int i = 0; i < 4; i++) {
			attributes.removeColumn(attributeColumnsToHide.get(i));
		}
        
        this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				boolean isToBeClosed = true;
				if (settingsConfiguration.isChanged()) {
					int result = JOptionPane.showConfirmDialog(parent, 
        					"Do you want to save changes before closing?", 
        					"Configurations are not saved yet!", 
        					JOptionPane.YES_NO_CANCEL_OPTION);
    				if (result == 2) {
    					isToBeClosed = false;
    				} else if (result == 0) {
    					saveConfigurationsToFile(file);
    					propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
    				}
				}
				if (isToBeClosed) {
					readyToValidate = false;
					setVisible(false);
				}
			}
		});
        this.setJMenuBar(buildMenuBar(parent));
        this.setContentPane(main);
    	this.setLocationRelativeTo(parent);
    	this.pack();
    	this.setVisible(true);
	}
	
	private void openSaveDialog(JFrame parent) {
		JFileChooser fileChooser = new JFileChooser();
		if (model.getModelDirectory() != null) {
			fileChooser = new JFileChooser(model.getModelDirectory().getPath());
		} else {
			fileChooser = new JFileChooser();
		}
		fileChooser.setFileFilter(new ExtFileFilter("properties", "Properties files"));
		fileChooser.setSelectedFile(file);

		if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File chosenFileName = fileChooser.getSelectedFile();
			if (chosenFileName.exists()) {
				int result = JOptionPane.showConfirmDialog(parent, "Do you want to overwrite the existing file?", "File already exists!", JOptionPane.OK_CANCEL_OPTION);
				if (result == 0) {
					saveConfigurationsToFile(chosenFileName);
				}
			} else {
				saveConfigurationsToFile(chosenFileName);
			}
		}
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
			e.printStackTrace();
			JOptionPane.showMessageDialog(getParent(), new JLabel("Error while loading properties file!"), "Error!", JOptionPane.ERROR_MESSAGE);
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
				String section = "config";
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
	
	private void setAllDefault() {
		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
		sectionSelectionComboBox.removeAllItems();
		selectedSection = "default";
		
		ChangeConfiguration.toDefaultSettings(settingsConfiguration);
		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
		
		propertiesConfigurationSections.put(selectedSection, propertiesConfiguration);
		sectionSelectionComboBox.addItem(selectedSection);
		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
	}

	public PropertiesConfiguration getChosenPropertiesConfiguration() {
		return propertiesConfiguration;
	}
	
	public Boolean isReadyToValidate() {
		return readyToValidate;
	}
	
	private JMenuBar buildMenuBar(final JFrame parent) {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
		JMenu configurationMenu = new JMenu("Configuration");
		JMenuItem renameMenuItem = new JMenuItem("Rename");
		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem cloneMenuItem = new JMenuItem("Clone");
		JMenuItem validateMenuItem = new JMenuItem("Validate");
		
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
        			ChangeConfiguration.clearSettings(settingsConfiguration);
        			ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
        			TableBuilder.repaintAllTables(tables.iterator());
        			settingsConfiguration.setChanged(false);
        			classes.clearSelection();
        			currentFileLabel.setText(file.getAbsolutePath());
        		}
        		
        	};
		});
		
		saveMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (file.exists()) {
        			saveConfigurationsToFile(file);
        		} else {
        			openSaveDialog(parent);
        		}
        	}
        });
		
		saveAsMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		openSaveDialog(parent);
        	}
        });
		
		renameMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sectionToDelete = selectedSection;
        		String newName = JOptionPane.showInputDialog(
        				"Please input the new name of this configuration:", 
        				selectedSection);
        		if (newName != null && !newName.equals("")) {
	        		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
	        		propertiesConfigurationSections.put(newName, (PropertiesConfiguration) propertiesConfiguration.clone());
	        		propertiesConfigurationSections.remove(sectionToDelete);
	        		selectedSection = newName;
	        		settingsConfiguration.setChanged(true);
	        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
	        		sectionSelectionComboBox.addItem(newName);
	        		sectionSelectionComboBox.removeItem(sectionToDelete);
	        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
	        		sectionSelectionComboBox.setSelectedItem(newName);
        		}
        	}
        });
		
		deleteMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (propertiesConfigurationSections.size() > 1) {
	        		String currentSection = (String) sectionSelectionComboBox.getSelectedItem();
	        		int currentIndex = sectionSelectionComboBox.getSelectedIndex();
	        		String futureSection;
	        		int futureIndex;
	        		if (currentIndex > 0) {
	        			futureIndex = currentIndex-1;
	        		} else {
	        			futureIndex = 1;
	        		}
	        		futureSection = sectionSelectionComboBox.getItemAt(futureIndex);
	        		selectedSection = futureSection;
	        		ChangeConfiguration.clearSettings(settingsConfiguration);
	        		ChangeConfiguration.toSettings(model, propertiesConfigurationSections.get(selectedSection), settingsConfiguration);
	        		propertiesConfigurationSections.remove(currentSection);
	        		sectionSelectionComboBox.setSelectedItem(selectedSection);
	        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
	        		sectionSelectionComboBox.removeItem(currentSection);
	        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        		} else { 
        			propertiesConfigurationSections.clear();
        			selectedSection = "default";
					ChangeConfiguration.toDefaultSettings(settingsConfiguration);
					propertiesConfigurationSections.put(selectedSection, 
							(ChangeConfiguration.toProperties(settingsConfiguration, model)));
					sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
					sectionSelectionComboBox.removeAllItems();
					sectionSelectionComboBox.addItem(selectedSection);
					sectionSelectionComboBox.addActionListener(comboBoxActionListener);
					sectionSelectionComboBox.setSelectedItem(selectedSection);
        		}
        		settingsConfiguration.setChanged(true);
        	}
        });
		
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog("Please input the name of the new configuration:", 
						DEFAULT_CONFIG_NAME + defaultNameCount);
				defaultNameCount++;
				if (newName != null && !newName.equals("")) {
					PropertiesConfiguration pc = ChangeConfiguration.toProperties(settingsConfiguration, model);
					propertiesConfigurationSections.put(selectedSection,pc);
					ChangeConfiguration.toDefaultSettings(settingsConfiguration);
					propertiesConfigurationSections.put(newName, 
							(ChangeConfiguration.toProperties(settingsConfiguration, model)));
					selectedSection = newName;
					settingsConfiguration.setChanged(true);
					sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
					sectionSelectionComboBox.addItem(newName);
					sectionSelectionComboBox.addActionListener(comboBoxActionListener);
					sectionSelectionComboBox.setSelectedItem(newName);
				}
			}
		});
		
		cloneMenuItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String newName = JOptionPane.showInputDialog("Please input the name of the cloned configuration:", 
        				DEFAULT_CONFIG_NAME+defaultNameCount);
        		defaultNameCount++;
        		if (newName != null && !newName.equals("")) {
        			propertiesConfigurationSections.put(newName, 
        					(PropertiesConfiguration) propertiesConfiguration.clone());
        		} else {
        			return;
        		}
        		selectedSection = newName;
        		settingsConfiguration.setChanged(true);
        		sectionSelectionComboBox.removeActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.addItem(newName);
        		sectionSelectionComboBox.addActionListener(comboBoxActionListener);
        		sectionSelectionComboBox.setSelectedItem(newName);
        	}
        });
		
		validateMenuItem.addActionListener(validateActionListener);
		
		
		
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
		basicTypesAndOptionsPanel.setDividerLocation(512);
		
		return basicTypesAndOptionsPanel;
	}

	private JSplitPane buildClassesAndAssociationsTab() {
		JTextArea abstractClassesText = new JTextArea();
		abstractClassesText.setBackground(this.getBackground());
		abstractClassesText.setText(abstractClassesChildren(this.model));
		abstractClassesText.setLineWrap(true);
		abstractClassesText.setWrapStyleWord(true);
		abstractClassesText.setCaretPosition(0);
		attributesLabel = new JLabel(ConfigurationTerms.ATTRIBUTES);
        associationsLabel = new JLabel(ConfigurationTerms.ASSOCIATIONS);
		
		JScrollPane classesScrollPane = new JScrollPane(classes);
		JScrollPane abstractClsScrollPane = new JScrollPane(abstractClassesText);
        JScrollPane attributesScrollPane = new JScrollPane(attributes);
        JScrollPane associationsScrollPane = new JScrollPane(associations);
        JPanel classesPanel = new JPanel(new BorderLayout());
        JPanel attributesPanel = new JPanel(new BorderLayout());
        JPanel associationsPanel = new JPanel(new BorderLayout());
        JPanel attributeLabelPanel = new JPanel();
        JSplitPane caaTabMainSplitPane;
        JSplitPane caaTabLeftSplitPane;
        JSplitPane caaTabRightSplitPane;
        
        classesScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,(this.getHeight())));
        abstractClsScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/4));
        attributesScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/2));
        associationsScrollPane.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/2));
        classesPanel.add(new JLabel("Classes"), BorderLayout.NORTH);
        classesPanel.add(classesScrollPane, BorderLayout.CENTER);
        attributeLabelPanel.setLayout(new BoxLayout(attributeLabelPanel, BoxLayout.LINE_AXIS));
        attributeLabelPanel.add(attributesLabel);
        attributeLabelPanel.add(Box.createHorizontalGlue());
        attributeLabelPanel.add(attributeCheckBox);
        attributesPanel.add(attributeLabelPanel, BorderLayout.NORTH);
        attributesPanel.add(attributesScrollPane, BorderLayout.CENTER);
        associationsPanel.add(associationsLabel, BorderLayout.NORTH);
        associationsPanel.add(associationsScrollPane, BorderLayout.CENTER);
        caaTabRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, attributesPanel, associationsPanel);
        caaTabLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, classesPanel, abstractClsScrollPane);
        caaTabMainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, caaTabLeftSplitPane, caaTabRightSplitPane);
		return caaTabMainSplitPane;
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
	
	private void saveConfigurationsToFile(File file) {
		try {
			propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
			propertiesConfigurationSections.put(selectedSection,(PropertiesConfiguration) propertiesConfiguration.clone());
			PropertiesWriter.writeToFile(propertiesConfigurationSections, file, model);
			settingsConfiguration.setChanged(false);
			currentFileLabel.setText(file.getAbsolutePath());
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
