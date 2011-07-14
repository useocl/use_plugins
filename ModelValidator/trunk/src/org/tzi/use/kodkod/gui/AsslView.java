package org.tzi.use.kodkod.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.gen.assl.statics.GProcedure;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.View;
import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.kodkod.assl.GGeneratorKodkod;
import org.tzi.use.kodkod.main.OCLInvar;
import org.tzi.use.kodkod.main.UMLAssociation;
import org.tzi.use.kodkod.main.UMLAssociationClass;
import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;


/**
 * AsslView controlls the ASSL-KodKod generator
 * 
 * @author Juergen Widdermann
 */
public class AsslView extends JPanel implements View {
	private static final long serialVersionUID = 9L;
	
	private MainWindow curMainWindow;
	private final JPanel myButtonPanel;
	private final JButton myStartButton;
	private final JButton myCancelButton;
	private final JTextField myFilePath;
	private final JButton myFileChooseButton;
	private final JTable myClaTable;
	private final JTable myAssTable;
	private final JTable myAssClaTable;
	private final JTable myOCLTable;
	private final JTable myParameterTable;
	private final PrintWriter myLogWriter;
	private final JPanel myFileInputPanel;
	private final JComboBox myComboBox;
	private final JFileChooser myFileChooser;
	private JPanel myAsslPanel;
	private JPanel progressBarPanel;
	private JPanel centerPanel;
	private JPanel myFileInput;
	private JPanel myPathTextPanel;
	private JPanel myStatusTextPanel;
	private JPanel myStatus;
	private JPanel myProcedureTextPanel;
	private JPanel myCombo;
	private JButton myFileLoadButton;
	private GGeneratorKodkod asslGen;
	private JPanel myComboBoxPanel;
	private JPanel myParameterTablePanel;
	private JProgressBar progressBar;
	private JLabel myPathText;
	private JLabel myProcedureText;
	private JLabel myParameterText;
	private JLabel myStatusText;

	private DefaultTableCellRenderer myRenderer;

	private JPanel myParameterTextPanel;

	private JPanel myParameter;

	
	
	public AsslView(MainWindow win, JTable myClaTable, JTable myAssTable, JTable myAssClaTable, JTable myOCLTable, MSystem curSystem, PrintWriter myLogWriter){
		super(new BorderLayout());
		
		//Creates the GUI
		
		curMainWindow = win;
		myFileInputPanel = new JPanel();
		myFileInputPanel.setLayout(new FlowLayout());
		
		myButtonPanel = new JPanel();
		myButtonPanel.setLayout(new FlowLayout());
		
		myStartButton = new JButton();
		myStartButton.setText("start");
		myStartButton.setPreferredSize(new Dimension(100, 25));
		myStartButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				myStartButton.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				progressBar.setIndeterminate(true);
				applyChanges();
			}
		});
		
		myCancelButton = new JButton();
		myCancelButton.setText("cancel");
		myCancelButton.setPreferredSize(new Dimension(100, 25));
		myCancelButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				close();
			}
		});
		
		myFileChooser = new JFileChooser();
		myFileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			
			@Override
			public String getDescription() {
				return "*.assl - ASSL-Files";
			}
			
			@Override
			public boolean accept(File f) {
				if(f.getName().endsWith(".assl")) {
					return true;
				}
				return false;
			}
		});
		
		myFileChooseButton = new JButton();
		myFileChooseButton.setText("...");
		myFileChooseButton.setPreferredSize(new Dimension(20,25));
		myFileChooseButton.setToolTipText("Loads an ASSL-Procedure");
		myFileChooseButton.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				chooseFile();
			}
		});
		
		myFileLoadButton = new JButton();
		myFileLoadButton.setText("Load");
		myFileLoadButton.setPreferredSize(new Dimension(75,25));
		myFileLoadButton.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				loadFile();
			}
		});
		
		myPathText = new JLabel();
		myPathText.setText("Path to ASSL-File");
		myPathTextPanel = new JPanel();
		myPathTextPanel.setLayout(new FlowLayout());
		myPathTextPanel.add(myPathText);
		
		myFilePath = new JTextField("");
		myFilePath.setPreferredSize(new Dimension(155,25));
		
		myFileInputPanel.add(myFilePath);
		myFileInputPanel.add(myFileChooseButton);
		myFileInputPanel.add(myFileLoadButton);
		
		myFileInput = new JPanel();
		myFileInput.setLayout(new BorderLayout());
		myFileInput.add(myPathTextPanel,BorderLayout.PAGE_START);
		myFileInput.add(myFileInputPanel,BorderLayout.CENTER);
		
		myAsslPanel = new JPanel();
		myAsslPanel.setLayout(new BorderLayout());
		
		myComboBox = new JComboBox();
		myComboBox.setPreferredSize(new Dimension(260, 25));
		myComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object item = e.getItem();
				if(item instanceof GProcedure) {
					myParameterTable.setModel(new AsslParameterTableModel((GProcedure) item));
					myParameterTable.setPreferredSize(new Dimension(260,myParameterTable.getRowHeight()*myParameterTable.getRowCount()));
				}
			}
		});
		
		myStatusText = new JLabel();
		myStatusText.setText("Progress");
		myStatusTextPanel = new JPanel();
		myStatusTextPanel.setLayout(new FlowLayout());
		myStatusTextPanel.add(myStatusText);
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(260, 25));
		
		progressBarPanel = new JPanel();
		progressBarPanel.add(progressBar);
		myStatus = new JPanel();
		myStatus.setLayout(new BorderLayout());
		myStatus.add(myStatusTextPanel,BorderLayout.PAGE_START);
		myStatus.add(progressBarPanel,BorderLayout.CENTER);
		
		myProcedureText = new JLabel();
		myProcedureText.setText("Choose Procedure");
		myProcedureTextPanel = new JPanel();
		myProcedureTextPanel.setLayout(new FlowLayout());
		myProcedureTextPanel.add(myProcedureText);
		myComboBoxPanel = new JPanel();
		myComboBoxPanel.add(myComboBox);
		myCombo = new JPanel();
		myCombo.setLayout(new BorderLayout());
		myCombo.add(myProcedureTextPanel,BorderLayout.PAGE_START);
		myCombo.add(myComboBoxPanel,BorderLayout.CENTER);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(myStatus, BorderLayout.PAGE_START);
		centerPanel.add(myCombo, BorderLayout.PAGE_END);
		
		
		myParameterText = new JLabel();
		myParameterText.setText("Parameter");
		myParameterTextPanel = new JPanel();
		myParameterTextPanel.setLayout(new FlowLayout());
		myParameterTextPanel.add(myParameterText);
		myParameterTable = new JTable();
		myParameterTable.setPreferredSize(new Dimension(260,0));
		myParameterTablePanel = new JPanel();
		myParameterTablePanel.add(myParameterTable);
		myRenderer = new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
			 */
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				this.setToolTipText((String)value.toString());
				this.setText((String)value.toString());
				return this;
			}
		};
		myParameterTable.setDefaultRenderer(String.class, myRenderer);
		myParameter = new JPanel();
		myParameter.setLayout(new BorderLayout());
		myParameter.add(myParameterTextPanel,BorderLayout.PAGE_START);
		myParameter.add(myParameterTablePanel,BorderLayout.CENTER);
		
		myAsslPanel.add(myFileInput, BorderLayout.PAGE_START);
		myAsslPanel.add(centerPanel, BorderLayout.CENTER);
		myAsslPanel.add(myParameter, BorderLayout.PAGE_END);
		
		myButtonPanel.add(myStartButton);
		myButtonPanel.add(myCancelButton);
		
		
		this.myClaTable = myClaTable;
		this.myAssTable = myAssTable;
		this.myAssClaTable = myAssClaTable;
		this.myOCLTable = myOCLTable;
		this.myLogWriter = myLogWriter;
		
		this.add(myAsslPanel, BorderLayout.NORTH);
		this.add(myButtonPanel, BorderLayout.CENTER);
		
		this.asslGen = new GGeneratorKodkod(curSystem, curMainWindow);
	}

	/**
	 * function starts assl-kodkod-translation
	 */
	public void applyChanges() {
		// generate formula
		HashMap<String, UMLClass> classes = new HashMap<String, UMLClass>();
		HashMap<String, UMLAssociation> associations = new HashMap<String, UMLAssociation>();
		HashMap<String, UMLAssociationClass> associationClasses = new HashMap<String, UMLAssociationClass>();
		ArrayList<OCLInvar> classInvariants = new ArrayList<OCLInvar>();
		for(int i = 0; i < myClaTable.getRowCount(); i++){
			classes.put(myClaTable.getValueAt(i, -2).toString(), (UMLClass) myClaTable.getValueAt(i, -1));
		}
		for(int i = 0; i < myAssTable.getRowCount(); i++){
			associations.put(myAssTable.getValueAt(i, -2).toString(), (UMLAssociation) myAssTable.getValueAt(i, -1));
		}
		for(int i = 0; i < myAssClaTable.getRowCount(); i++){
			associationClasses.put(myAssClaTable.getValueAt(i, -2).toString(), (UMLAssociationClass) myAssClaTable.getValueAt(i, -1));
		}
		for(int i = 0; i < myOCLTable.getRowCount(); i++){
			classInvariants.add((OCLInvar) myOCLTable.getValueAt(i, -1));
		}
		AsslTranslation translation = new AsslTranslation();
		String procedureCall = ((GProcedure)myComboBox.getSelectedItem()).name()+"(";
		// add Parameters for Procedure
		int j = myParameterTable.getModel().getRowCount();
		String komma = "";
		for(int i = 0; i < j; i++){
			procedureCall += komma + myParameterTable.getModel().getValueAt(i, 1);
			komma = ",";
		}
		procedureCall += ")";
		asslGen.setKodKodContext(classes, associations, associationClasses, classInvariants, myLogWriter, translation);
		AsslTranslationRunnable task = new AsslTranslationRunnable(asslGen, myFilePath.getText(), procedureCall, curMainWindow, this);
		task.execute();
	}
	
	public JButton getStartButton() {
		return myStartButton;
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public void close() {
		this.setEnabled(false);
	}
	
	public void stateChanged(StateChangeEvent e){
		
	}
	
	public void detachModel(){
		
	}
	
	/**
	 * Opens the Filechooser
	 */
	protected void chooseFile() {
		int value = myFileChooser.showOpenDialog(this);
		if(value == JFileChooser.APPROVE_OPTION) {
			myFilePath.setText(myFileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	/**
	 * loads the assl file
	 */
	public void loadFile() {
		List<GProcedure> procs = asslGen.loadAsslFile(myFilePath.getText());
		myComboBox.removeAllItems();
		if(procs != null) {
			for(GProcedure proc : procs){
				myComboBox.addItem(proc);
			}
		}
	}
}
