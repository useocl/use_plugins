package org.tzi.use.kodkod.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.model.config.ConfigurationFileManager;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.util.ChangeConfiguration;
import org.tzi.use.util.StringUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 *  A GUI for adjusting the configurations before giving them to the model validator
 *
 * @author Subi Aili
 * @author Frank Hilken
 */
public class ModelValidatorConfigurationWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String WINDOW_TITLE = "Model Validator Configuration";
	
	private final IModel model;
	private File currentFile;
	private SettingsConfiguration settingsConfiguration;
	private ConfigurationFileManager configManager;
	private Configuration propertiesConfiguration;
	
	private String selectedSection;
	private IClass selectedClass;
	private boolean readyToValidate;
	
	private final JComboBox<String> sectionSelectionComboBox;
	
	private JLabel attributesLabel;
	private JLabel associationsLabel;
	private final JLabel currentFileLabel;
	private final JTextArea statusArea;

	private final JTable invariantsTable;
	private final JTable optionsTable;
	private final JTable associationTable;
	private final JTable attributeTable;
	private final JTable classTable;
	private final JTable integerTable;
	private final JTable realTable;
	private final JTable stringTable;
	private final JCheckBox attributeCheckBox;
	private JCheckBox integerCheckbox;
	private JCheckBox stringCheckbox;
	private JCheckBox realCheckbox;

	private final JTabbedPane center;
	private final JPanel main;
	private final JPanel northNorth;
	private final JPanel northSouth;
	private final JPanel north;
	private final JPanel southWest;
	private final JPanel southCenter;
	private final JPanel south;
	private final JButton validateButton;

	/*
	 * Listens for changed class row selection in the class table
	 */
	private class ClassTableSelectionHandler implements ListSelectionListener {
		private IClass currentClass = null;
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			int selectedRow = 0;
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					selectedRow = i;
					break;
				}
			}
			
			selectedClass = (IClass) classTable.getValueAt(selectedRow, 0);
			if(currentClass != null && currentClass.equals(selectedClass)){
				return;
			}
			attributesLabel.setText("Attributes of class " + selectedClass.name());
			associationsLabel.setText("Associations of class " + selectedClass.name());
			updateClassAttributes(selectedClass);
			updateClassAssociations(selectedClass);
			currentClass = selectedClass;
		}
	}
	
	private class ValidateActionListener implements ActionListener {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if (settingsConfiguration.isChanged()) {
				int result = JOptionPane.showConfirmDialog(ModelValidatorConfigurationWindow.this,
						"Do you want to save them before Validation?",
						"Configurations are not saved yet!",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
					return;
				} else if (result == JOptionPane.YES_OPTION) {
					saveConfigurationsToFile(currentFile);
				}
			}
			propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
			readyToValidate = true;
			dispose();
		}
	}

	public ModelValidatorConfigurationWindow(final JFrame parent, final IModel model, final String useFile) {
		super(parent, WINDOW_TITLE);
		this.model = model;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(1024,300);

		settingsConfiguration = new SettingsConfiguration(model);
		TableBuilder tableBuilder = new TableBuilder(settingsConfiguration);

		integerTable = tableBuilder.integer();
		realTable = tableBuilder.real();
		stringTable = tableBuilder.string();
		optionsTable = tableBuilder.options();
		classTable = tableBuilder.classes();
		attributeTable = tableBuilder.attributes();
		associationTable = tableBuilder.associations();
		invariantsTable = tableBuilder.invariants();

		classTable.getSelectionModel().addListSelectionListener(new ClassTableSelectionHandler());

		readyToValidate = false;

		center = new JTabbedPane(SwingConstants.TOP);
		main = new JPanel(new BorderLayout());
		northNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northSouth = new JPanel(new FlowLayout(FlowLayout.LEFT));
		north = new JPanel(new BorderLayout());
		southWest = new JPanel(new FlowLayout(FlowLayout.LEFT));
		southCenter = new JPanel(new BorderLayout());
		southCenter.setBorder(new BevelBorder(BevelBorder.LOWERED));
		south = new JPanel(new BorderLayout());

		sectionSelectionComboBox = new JComboBox<String>();

		attributeCheckBox = new JCheckBox("Show specific bounds", true);
		attributeCheckBox.addItemListener(new ItemListener() {
			
			private TableColumn[] attributeColumnsToHide = new TableColumn[]{
				attributeTable.getColumn(ConfigurationTerms.ATTRIBUTES_MIN),
				attributeTable.getColumn(ConfigurationTerms.ATTRIBUTES_MAX),
				attributeTable.getColumn(ConfigurationTerms.ATTRIBUTES_MINSIZE),
				attributeTable.getColumn(ConfigurationTerms.ATTRIBUTES_MAXSIZE)
			};
			private boolean isExpanded = attributeCheckBox.isSelected();
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED && !isExpanded) {
					for (int i = 0; i < attributeColumnsToHide.length; i++) {
						attributeTable.addColumn(attributeColumnsToHide[i]);
					}
					// reorder table to place added columns "in the middle"
					attributeTable.moveColumn(attributeTable.getColumnModel().getColumnIndex(ConfigurationTerms.ATTRIBUTES_VALUES), attributeTable.getColumnCount()-1);
					isExpanded = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED && isExpanded) {
					for (int i = 0; i < attributeColumnsToHide.length; i++) {
						attributeTable.removeColumn(attributeColumnsToHide[i]);
					}
					isExpanded = false;
				}
			}
		});
		attributeCheckBox.setSelected(false);
		
		validateButton = new JButton("Validate");
		validateButton.addActionListener(new ValidateActionListener());

		statusArea = new JTextArea();
		statusArea.setEditable(false);
		statusArea.setBackground(getParent().getBackground());
		statusArea.setLineWrap(true);
		statusArea.setWrapStyleWord(true);
		statusArea.setText("");

		northNorth.add(new JLabel("Loaded properties file: "));
		currentFileLabel = new JLabel();
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
		main.add(north, BorderLayout.NORTH);
		main.add(center, BorderLayout.CENTER);
		main.add(south, BorderLayout.SOUTH);

		sectionSelectionComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					if (propertiesConfiguration != null) {
						propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
						configManager.addOrUpdateConfiguration(selectedSection, propertiesConfiguration);
					}
					selectedSection = (String) e.getItem();
					propertiesConfiguration = configManager.getConfiguration(selectedSection);
					boolean beforeChange = settingsConfiguration.isChanged();
					ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
					settingsConfiguration.setChanged(beforeChange);
					update();
				}
			}
		});
		if(classTable.getModel().getRowCount() > 0){
			classTable.getSelectionModel().setSelectionInterval(0, 0);
		}
		
		currentFile = new File(useFile.replaceAll("\\.use", "") + ".properties");
		loadConfigurations(currentFile);
		ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);

		settingsConfiguration.setChanged(false);
		
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // increases tooltip display time
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				close();
			}
		});
		setJMenuBar(buildMenuBar());
		setContentPane(main);
		update();
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void showSaveAsDialog() {
		JFileChooser fileChooser = new JFileChooser();

		if (currentFile != null) {
			fileChooser = new JFileChooser(currentFile.getParentFile());
		} else {
			fileChooser = new JFileChooser();
		}
		fileChooser.setFileFilter(new ExtFileFilter("properties", "Properties files"));
		fileChooser.setSelectedFile(currentFile);

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File chosenFile = fileChooser.getSelectedFile();
			int result = JOptionPane.OK_OPTION;
			if (chosenFile.exists()) {
				result = JOptionPane.showConfirmDialog(this, "Do you want to overwrite the existing file?", "File already exists!", JOptionPane.OK_CANCEL_OPTION);
			}
			if (result == JOptionPane.OK_OPTION) {
				saveConfigurationsToFile(chosenFile);
				currentFile = chosenFile;
				currentFileLabel.setText(currentFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Loads a .properties-File and sets up the GUI with the sections.
	 * 
	 * @return true, if given file was successfully loaded; false, otherwise
	 */
	private boolean loadConfigurations(File file) {
		boolean ret;
		try {
			configManager = new ConfigurationFileManager(model, settingsConfiguration, file);
			currentFileLabel.setText(file.getAbsolutePath());
			ret = true;
			setTitle(file.getName() + " - " + WINDOW_TITLE);
		} catch (ConfigurationException e) {
			if(configManager == null){
				// first open of GUI
				configManager = new ConfigurationFileManager(model, settingsConfiguration);
				ret = false;
				JOptionPane.showMessageDialog(null, "Error while loading properties file! Switching to default configuration.", "Error!", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Error while loading properties file! Staying with current configuration.", "Error!", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		refreshSectionSelection();
		
		return ret;
	}

	public Configuration getChosenConfiguration() {
		return propertiesConfiguration;
	}

	public Boolean isReadyToValidate() {
		return readyToValidate;
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				JFileChooser fileChooser = new JFileChooser();
				if (currentFile != null) {
					fileChooser = new JFileChooser(currentFile.getParentFile());
				} else {
					fileChooser = new JFileChooser();
				}
				fileChooser.setFileFilter(new ExtFileFilter("properties", "Properties files"));

				if (fileChooser.showOpenDialog(ModelValidatorConfigurationWindow.this) == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					boolean success = loadConfigurations(currentFile);
					if(success){
						ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
						classTable.clearSelection();
						settingsConfiguration.setChanged(false);
						update();
					}
				}

			}
		});
		fileMenu.add(openMenuItem);

		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentFile.exists()) {
					saveConfigurationsToFile(currentFile);
				} else {
					showSaveAsDialog();
				}
			}
		});
		fileMenu.add(saveMenuItem);

		JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
		saveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSaveAsDialog();
			}
		});
		fileMenu.add(saveAsMenuItem);
		
		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		fileMenu.add(closeMenuItem);

		JMenu menuConfiguration = new JMenu("Configuration");
		
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = readNewConfigurationName("Please input the name of the new configuration:", configManager.createNewConfigName(ConfigurationFileManager.DEFAULT_CONFIG_PREFIX));
				if(newName == null){
					return;
				}
				
				Configuration pc = ChangeConfiguration.toProperties(settingsConfiguration, model);
				configManager.addOrUpdateConfiguration(selectedSection, pc);
				
				settingsConfiguration.reset();
				configManager.addOrUpdateConfiguration(newName, ChangeConfiguration.toProperties(settingsConfiguration, model));
				
				settingsConfiguration.setChanged(true);
				sectionSelectionComboBox.addItem(newName);
				switchToConfiguration(newName, false);
			}
		});
		menuConfiguration.add(newMenuItem);

		JMenuItem cloneMenuItem = new JMenuItem("Clone");
		cloneMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = readNewConfigurationName("Please input the name of the cloned configuration:", selectedSection);
				if (newName == null) {
					return;
				}
				
				Configuration c = ChangeConfiguration.toProperties(settingsConfiguration, model);
				configManager.addOrUpdateConfiguration(newName, c);
				
				settingsConfiguration.setChanged(true);
				sectionSelectionComboBox.addItem(newName);
				switchToConfiguration(newName, false);
			}
		});
		menuConfiguration.add(cloneMenuItem);
		
		JMenuItem renameMenuItem = new JMenuItem("Rename");
		renameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sectionToDelete = selectedSection;
				String newName = readNewConfigurationName("Please input the new name of this configuration:", selectedSection);
				if(newName == null){
					return;
				}
				
				propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
				
				configManager.addOrUpdateConfiguration(newName, propertiesConfiguration);
				configManager.removeConfiguration(sectionToDelete);
				
				settingsConfiguration.setChanged(true);
				sectionSelectionComboBox.addItem(newName);
				switchToConfiguration(newName, false);
				sectionSelectionComboBox.removeItem(sectionToDelete);
			}
		});
		menuConfiguration.add(renameMenuItem);

		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean needToRefresh = configManager.getConfigutationCount() == 1;
				
				String removedSelection = selectedSection;
				configManager.removeConfiguration(removedSelection);
				propertiesConfiguration = null;
				selectedSection = null;
				
				settingsConfiguration.setChanged(true);
				if(needToRefresh){
					refreshSectionSelection();
				} else {
					int newIndex = sectionSelectionComboBox.getSelectedIndex() == 0 ? 1 : 0;
					sectionSelectionComboBox.setSelectedIndex(newIndex);
					sectionSelectionComboBox.removeItem(removedSelection);
				}
				ChangeConfiguration.toSettings(model, propertiesConfiguration, settingsConfiguration);
			}
		});
		menuConfiguration.add(deleteMenuItem);

		menuConfiguration.add(new JSeparator());
		
		JMenuItem validateMenuItem = new JMenuItem("Validate");
		validateMenuItem.addActionListener(new ValidateActionListener());
		menuConfiguration.add(validateMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(menuConfiguration);

		return menuBar;
	}

	private void switchToConfiguration(String newName, boolean saveCurrent) {
		if(!saveCurrent){
			propertiesConfiguration = null;
			selectedSection = null;
		}
		sectionSelectionComboBox.setSelectedItem(newName);
	}

	private JSplitPane buildBasicTypesAndOptionsTab() {
		JSplitPane basicTypesAndOptionsPanel;

		JPanel leftUpper = new JPanel(new BorderLayout());
		leftUpper.add(basicTypesPanel(), BorderLayout.NORTH);
		JPanel rightUpper = new JPanel(new BorderLayout());
		rightUpper.add(new JScrollPane(optionsTable), BorderLayout.CENTER);

		JPanel rightLower = new JPanel(new BorderLayout());
		JTextArea legendText = new JTextArea(LegendEntry.LEGEND);
		legendText.setEditable(false);
		legendText.setBackground(getParent().getBackground());
		legendText.setLineWrap(true);
		legendText.setWrapStyleWord(true);
		JScrollPane legenScroll = new JScrollPane(legendText);
		rightLower.add(legenScroll, BorderLayout.CENTER);

		JPanel right = new JPanel(new BorderLayout());
		right.add(rightUpper, BorderLayout.NORTH);
		right.add(rightLower, BorderLayout.CENTER);

		JScrollPane jScrollPane = new JScrollPane(leftUpper);
		jScrollPane.setBorder(null);
		
		basicTypesAndOptionsPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jScrollPane, right);
		basicTypesAndOptionsPanel.setDividerLocation(512);

		return basicTypesAndOptionsPanel;
	}

	private JPanel basicTypesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		Dimension space = new Dimension(0, 10);

		final JPanel integerPanel = new JPanel(new BorderLayout());
		final JPanel integerContent = new JPanel(new BorderLayout());
		final JScrollPane intScroll = new JScrollPane(integerTable);
		integerCheckbox = new JCheckBox(TypeConstants.INTEGER, settingsConfiguration.getIntegerTypeSettings().isEnabled());
		Font captionFont = integerCheckbox.getFont().deriveFont(Font.BOLD, 12f);
		integerCheckbox.setFont(captionFont);
		integerCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean on = e.getStateChange() == ItemEvent.SELECTED;
				integerContent.setVisible(on);
				settingsConfiguration.getIntegerTypeSettings().setEnabled(on);
				integerPanel.revalidate();
			}
		});
		
		integerContent.add(intScroll, BorderLayout.CENTER);
		integerContent.add(new JLabel("Here will be more information about what integers are actually generated by the settings."), BorderLayout.SOUTH);
		integerContent.setVisible(settingsConfiguration.getIntegerTypeSettings().isEnabled());
		integerPanel.add(integerCheckbox, BorderLayout.NORTH);
		integerPanel.add(integerContent, BorderLayout.CENTER);
		panel.add(integerPanel);

		panel.add(Box.createRigidArea(space));
		
		final JPanel stringPanel = new JPanel(new BorderLayout());
		final JPanel stringContent = new JPanel(new BorderLayout());
		final JScrollPane stringScroll = new JScrollPane(stringTable);
		stringCheckbox = new JCheckBox(TypeConstants.STRING, settingsConfiguration.getStringTypeSettings().isEnabled());
		stringCheckbox.setFont(captionFont);
		stringCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean on = e.getStateChange() == ItemEvent.SELECTED;
				stringContent.setVisible(on);
				settingsConfiguration.getStringTypeSettings().setEnabled(on);
				stringPanel.revalidate();
			}
		});

		stringContent.add(stringScroll, BorderLayout.CENTER);
		stringContent.setVisible(settingsConfiguration.getStringTypeSettings().isEnabled());
		stringPanel.add(stringCheckbox, BorderLayout.NORTH);
		stringPanel.add(stringContent, BorderLayout.CENTER);
		panel.add(stringPanel);
		
		panel.add(Box.createRigidArea(space));
		
		final JPanel realPanel = new JPanel(new BorderLayout());
		final JPanel realContent = new JPanel(new BorderLayout());
		final JScrollPane realScroll = new JScrollPane(realTable);
		realCheckbox = new JCheckBox(TypeConstants.REAL, settingsConfiguration.getRealTypeSettings().isEnabled());
		realCheckbox.setFont(captionFont);
		realCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean on = e.getStateChange() == ItemEvent.SELECTED;
				realContent.setVisible(on);
				settingsConfiguration.getRealTypeSettings().setEnabled(on);
				realPanel.revalidate();
			}
		});
		
		realContent.add(realScroll, BorderLayout.CENTER);
		realContent.setVisible(settingsConfiguration.getRealTypeSettings().isEnabled());
		realPanel.add(realCheckbox, BorderLayout.NORTH);
		realPanel.add(realContent, BorderLayout.CENTER);
		panel.add(realPanel);

		return panel;
	}

	private JSplitPane buildClassesAndAssociationsTab() {
		JTextArea abstractClassesText = new JTextArea();
		abstractClassesText.setBackground(getBackground());
		abstractClassesText.setText(abstractClassesChildren(model));
		abstractClassesText.setLineWrap(true);
		abstractClassesText.setWrapStyleWord(true);
		attributesLabel = new JLabel("Attributes");
		associationsLabel = new JLabel("Associations");
		
		Font captionFont = attributesLabel.getFont().deriveFont(Font.BOLD, 12f);
		attributesLabel.setFont(captionFont);
		associationsLabel.setFont(captionFont);

		JScrollPane classesScrollPane = new JScrollPane(classTable);
		JScrollPane abstractClsScrollPane = new JScrollPane(abstractClassesText);
		JScrollPane attributesScrollPane = new JScrollPane(attributeTable);
		JScrollPane associationsScrollPane = new JScrollPane(associationTable);
		JPanel classesPanel = new JPanel(new BorderLayout());
		JPanel attributesPanel = new JPanel(new BorderLayout());
		JPanel associationsPanel = new JPanel(new BorderLayout());
		JPanel attributeLabelPanel = new JPanel(new BorderLayout());
		JSplitPane caaTabMainSplitPane;
		JSplitPane caaTabLeftSplitPane;
		JSplitPane caaTabRightSplitPane;

		classesScrollPane.setPreferredSize(new Dimension(getWidth()/2, getHeight()));
		abstractClsScrollPane.setPreferredSize(new Dimension(getWidth()/2, getHeight()/4));
		attributesScrollPane.setPreferredSize(new Dimension(getWidth()/2, getHeight()/2));
		associationsScrollPane.setPreferredSize(new Dimension(getWidth()/2, getHeight()/2));
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
		JScrollPane invariantsScrollPane = new JScrollPane(invariantsTable);
		invariantsScrollPane.setPreferredSize(new Dimension(getWidth()/2,getHeight()));
		JPanel invariantsPanel = new JPanel(new BorderLayout());
		invariantsPanel.add(invariantsScrollPane, BorderLayout.CENTER);

		return invariantsPanel;
	}

	/**
	 * updates the attributes table referring to given className and gets them from the SettingsConfiguration
	 */
	private void updateClassAttributes(IClass className) {
		TableModelAttribute attributeModel = (TableModelAttribute)attributeTable.getModel();
		attributeModel.setClass(settingsConfiguration.getClassSettings(className));
	}

	private void updateClassAssociations(IClass className) {
		TableModelAssociation associationModel = (TableModelAssociation)associationTable.getModel();
		associationModel.setClass(settingsConfiguration.getClassSettings(className));
	}

	/**
	 * Asks the user for a new configuration name.
	 * 
	 * @return name of configuration on success or null in case of abort or error
	 */
	private String readNewConfigurationName(String dialogMessage, String suggestedValue) {
		String newName;
		String error = null;
		do {
			newName = JOptionPane.showInputDialog(ModelValidatorConfigurationWindow.this, (error == null? "" : error) + dialogMessage, suggestedValue);
		
			if (newName == null || newName.isEmpty()) {
				return null;
			} else if(newName.length() > ConfigurationFileManager.MAX_CONFIGURATION_NAME_LENGTH) {
				error = "The given configuration name is too long (max length = " + ConfigurationFileManager.MAX_CONFIGURATION_NAME_LENGTH + ").\n";
				suggestedValue = newName;
				continue;
			} else if(configManager.isConfigNameTaken(newName)){
				error = "A configuration with that name already exists.\n";
				suggestedValue = newName;
				continue;
			}
			
			break;
		} while(true);
		
		return newName;
	}
	
	private void saveConfigurationsToFile(File file) {
		propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
		configManager.addOrUpdateConfiguration(selectedSection, propertiesConfiguration);
		
		try {
			configManager.save(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error while saving configuration to file!", "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		settingsConfiguration.setChanged(false);
		currentFileLabel.setText(file.getAbsolutePath());
		setTitle(file.getName() + " - " + WINDOW_TITLE);
	}

	private void refreshSectionSelection() {
		sectionSelectionComboBox.removeAllItems();
		
		for (String cName : configManager.getConfigurationNames()) {
			sectionSelectionComboBox.addItem(cName);
		}
		// the first added item is automatically selected
	}
	
	private void update() {
		integerTable.repaint();
		realTable.repaint();
		stringTable.repaint();
		optionsTable.repaint();
		classTable.repaint();
		attributeTable.repaint();
		associationTable.repaint();
		invariantsTable.repaint();
		
		integerCheckbox.setSelected(settingsConfiguration.getIntegerTypeSettings().isEnabled());
		stringCheckbox.setSelected(settingsConfiguration.getStringTypeSettings().isEnabled());
		realCheckbox.setSelected(settingsConfiguration.getRealTypeSettings().isEnabled());
	}
	
	private void close() {
		boolean isToBeClosed = true;
		if (settingsConfiguration.isChanged()) {
			int result = JOptionPane.showConfirmDialog(ModelValidatorConfigurationWindow.this,
					"Do you want to save changes before closing?",
					"Configurations are not saved yet!",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
				isToBeClosed = false;
			} else if (result == JOptionPane.YES_OPTION) {
				saveConfigurationsToFile(currentFile);
				propertiesConfiguration = ChangeConfiguration.toProperties(settingsConfiguration, model);
			}
		}
		if (isToBeClosed) {
			readyToValidate = false;
			dispose();
		}
	}
	
	private String abstractClassesChildren(IModel model) {
		StringBuilder abstractText = new StringBuilder("Abstract Classes:");
		abstractText.append(StringUtil.NEWLINE);

		Collection<IClass> abstractClasses = Collections2.filter(model.classes(), new Predicate<IClass>() {
			@Override
			public boolean apply(IClass input) {
				return input.isAbstract();
			}
		});
		if(abstractClasses.isEmpty()){
			abstractText.append("None.");
			return abstractText.toString();
		} else {
			StringUtil.fmtSeq(abstractText, abstractClasses, ", ");
		}

		if(abstractClasses.size() > 0){
			abstractText.append(StringUtil.NEWLINE);
			abstractText.append(StringUtil.NEWLINE);
			abstractText.append("Inheriting Classes:");
			abstractText.append(StringUtil.NEWLINE);

			for(final IClass abstractClass : model.classes()){
				if(abstractClass.allChildren().isEmpty()){
					continue;
				}
				Collection<IClass> inheritingClasses = abstractClass.allChildren();
				StringUtil.fmtSeq(abstractText, inheritingClasses, StringUtil.NEWLINE, new StringUtil.IElementFormatter<IClass>() {
					@Override
					public String format(IClass element) {
						return abstractClass.name() + " > " + element.name();
					}
				});

			}
		}

		return abstractText.toString();
	}

}
