package org.tzi.use.kodkod.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.util.TextComponentWriter;
import org.tzi.use.gui.views.View;
import org.tzi.use.kodkod.main.OCLInvar;
import org.tzi.use.kodkod.main.SetKodkodStruc;
import org.tzi.use.kodkod.main.UMLAssociation;
import org.tzi.use.kodkod.main.UMLAssociationClass;
import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * MainView of Kodkod-Plugin
 * shows classes, associations, associationclasses
 * and invariants of model and kodkod outcome
 * @author  Torsten Humann
 */
public class KodkodView extends JPanel implements View{
	private static final long serialVersionUID = 1L;
	
	private MainWindow curMainWindow;
	private final MSystem curSystem;
	private final JTabbedPane myTabPanel;
	private final JPanel myMainPanel;
	private final JPanel myButton1MainPanel;
	private final JPanel myButton2MainPanel;
	private final JPanel myButton3MainPanel;
	private final JPanel myButton4MainPanel;
	private final JPanel myButton5MainPanel;
	private final JPanel myClaButtonPanel;
	private final JPanel myClaPanel;
	private final JPanel myAssButtonPanel;
	private final JPanel myAssPanel;
	private final JPanel myAssClaButtonPanel;
	private final JPanel myAssClaPanel;
	private final JButton myRunButton;
	private final JButton myResetButton;
	private final JButton myIndButton;
	private final JButton mySolButton;
	private final JButton myAsslButton;
	private final JButton myClaResetButton;
	private final JButton myAssResetButton;
	private final JButton myAssClaResetButton;
	private final JTable myClaTable;
	private final JTable myAssTable;
	private final JTable myAssClaTable;
	private final JTable myOCLTable;
	private final JScrollPane myClaTablePane;
	private final JScrollPane myAssTablePane;
	private final JScrollPane myAssClaTablePane;
	private final JScrollPane myOCLTablePane;
	private final ClaTableModel myClaTableModel;
	private final AssTableModel myAssTableModel;
	private final AssClaTableModel myAssClaTableModel;
	private final OCLTableModel myOCLTableModel;
	private final TableColumn[] columnCla = new TableColumn[3];
	private final TableColumn[] columnAss = new TableColumn[3];
	private final TableColumn[] columnAssCla = new TableColumn[3];
	private final JPanel myLogPanel;
	private final JTextArea myTextArea;
	private final PrintWriter myLogWriter;
	private final JScrollPane myLogPane;
	
	private String UndefInfoInt = null;
	private String UndefInfo = null;
	private int solvNumber;
	
	public KodkodView(MainWindow curMainWin, MSystem system){
		super(new BorderLayout());
		solvNumber = 0;
		
		curMainWindow = curMainWin;
		
		curSystem = system;
		curSystem.addChangeListener(this);
		
		myTabPanel = new JTabbedPane();
		myMainPanel = new JPanel();
		myMainPanel.setLayout(new GridLayout(4,1));
		myButton1MainPanel = new JPanel();
		myButton1MainPanel.setLayout(new FlowLayout());
		myButton2MainPanel = new JPanel();
		myButton2MainPanel.setLayout(new FlowLayout());
		myButton3MainPanel = new JPanel();
		myButton3MainPanel.setLayout(new FlowLayout());
		myButton4MainPanel = new JPanel();
		myButton4MainPanel.setLayout(new FlowLayout());
		myButton5MainPanel = new JPanel();
		myButton5MainPanel.setLayout(new FlowLayout());
		myClaPanel = new JPanel();
		myClaPanel.setLayout(new BorderLayout());
		myAssPanel = new JPanel();
		myAssPanel.setLayout(new BorderLayout());
		myAssClaPanel = new JPanel();
		myAssClaPanel.setLayout(new BorderLayout());
		myClaButtonPanel = new JPanel();
		myClaButtonPanel.setLayout(new FlowLayout());
		myAssButtonPanel = new JPanel();
		myAssButtonPanel.setLayout(new FlowLayout());
		myAssClaButtonPanel = new JPanel();
		myAssClaButtonPanel.setLayout(new FlowLayout());
		
		myRunButton = new JButton();
		myRunButton.setText("run Model Validator");
		myRunButton.setPreferredSize(new Dimension(200, 25));
		myRunButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				startKodkod(false);
			}
		});
		
		myResetButton = new JButton();
		myResetButton.setText("reset bounds");
		myResetButton.setPreferredSize(new Dimension(200, 25));
		myResetButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				for(int i = 0; i < myClaTable.getRowCount(); i++){
					myClaTable.setValueAt(0, i, 1);
					myClaTable.setValueAt(0, i, 2);
				}
				for(int i = 0; i < myAssTable.getRowCount(); i++){
					myAssTable.setValueAt(0, i, 1);
					myAssTable.setValueAt(0, i, 2);
				}
				for(int i = 0; i < myAssClaTable.getRowCount(); i++){
					myAssClaTable.setValueAt(0, i, 1);
					myAssClaTable.setValueAt(0, i, 2);
				}
			}
		});
		
		myClaResetButton = new JButton();
		myClaResetButton.setText("reset class bounds");
		myClaResetButton.setPreferredSize(new Dimension(200, 25));
		myClaResetButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				for(int i = 0; i < myClaTable.getRowCount(); i++){
					myClaTable.setValueAt(0, i, 1);
					myClaTable.setValueAt(0, i, 2);
				}
			}
		});
		
		myAssResetButton = new JButton();
		myAssResetButton.setText("reset association bounds");
		myAssResetButton.setPreferredSize(new Dimension(200, 25));
		myAssResetButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				for(int i = 0; i < myAssTable.getRowCount(); i++){
					myAssTable.setValueAt(0, i, 1);
					myAssTable.setValueAt(0, i, 2);
				}
			}
		});
		
		myAssClaResetButton = new JButton();
		myAssClaResetButton.setText("reset associationclass bounds");
		myAssClaResetButton.setPreferredSize(new Dimension(200, 25));
		myAssClaResetButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				for(int i = 0; i < myAssClaTable.getRowCount(); i++){
					myAssClaTable.setValueAt(0, i, 1);
					myAssClaTable.setValueAt(0, i, 2);
				}
			}
		});
		
		myIndButton = new JButton();
		myIndButton.setText("test invariant independency");
		myIndButton.setPreferredSize(new Dimension(200, 25));
		myIndButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				startKodkod(true);
			}
		});
		
		mySolButton = new JButton();
		mySolButton.setText("change solver");
		mySolButton.setPreferredSize(new Dimension(200, 25));
		mySolButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				startSol();
			}
		});
		
		myAsslButton = new JButton();
		myAsslButton.setText("Assl");
		myAsslButton.setPreferredSize(new Dimension(200, 25));
		myAsslButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
		        showAsslWindow();
			}
		});
		
		myClaTableModel = new ClaTableModel(curSystem);
		myClaTable = new JTable(myClaTableModel);
		myClaTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myClaTable.setPreferredScrollableViewportSize(new Dimension(249, 40));
		myClaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myClaTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2 && myClaTable.getSelectedColumn() == 0){
					showNames(false);
				}
			}
		});
		myClaTablePane = new JScrollPane(myClaTable);
		for(int i = 0; i < columnCla.length; i++){
			columnCla[i] = myClaTable.getColumnModel().getColumn(i);
			columnCla[i].setCellRenderer(new MyColumn(true));
		}
		fitWidth(myClaTable);
		myClaTable.getTableHeader().setReorderingAllowed(false);
		
		myAssTableModel = new AssTableModel(curSystem);
		myAssTable = new JTable(myAssTableModel);
		myAssTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myAssTable.setPreferredScrollableViewportSize(new Dimension(249, 40));
		myAssTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myAssTablePane = new JScrollPane(myAssTable);
		for(int i = 0; i < columnAss.length; i++){
			columnAss[i] = myAssTable.getColumnModel().getColumn(i);
			columnAss[i].setCellRenderer(new MyColumn(false));
		}
		fitWidth(myAssTable);
		myAssTable.getTableHeader().setReorderingAllowed(false);
		
		myAssClaTableModel = new AssClaTableModel(curSystem);
		myAssClaTable = new JTable(myAssClaTableModel);
		myAssClaTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myAssClaTable.setPreferredScrollableViewportSize(new Dimension(249, 40));
		myAssClaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myAssClaTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2 && myAssClaTable.getSelectedColumn() == 0){
					showNames(true);
				}
			}
		});
		myAssClaTablePane = new JScrollPane(myAssClaTable);
		for(int i = 0; i < columnAssCla.length; i++){
			columnAssCla[i] = myAssClaTable.getColumnModel().getColumn(i);
			columnAssCla[i].setCellRenderer(new MyColumn(true));
		}
		fitWidth(myAssClaTable);
		myAssClaTable.getTableHeader().setReorderingAllowed(false);
		
		myOCLTableModel = new OCLTableModel(curSystem);
		myOCLTable = new JTable(myOCLTableModel);
		myOCLTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myOCLTable.setPreferredScrollableViewportSize(new Dimension(249, 40));
		myOCLTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myOCLTablePane = new JScrollPane(myOCLTable);
		fitWidth(myOCLTable);
		myOCLTable.getTableHeader().setReorderingAllowed(false);
		
		myButton1MainPanel.add(myRunButton);
		myButton2MainPanel.add(myIndButton);
		myButton3MainPanel.add(mySolButton);
		myButton4MainPanel.add(myResetButton);
		myButton5MainPanel.add(myAsslButton);
		
		myMainPanel.add(myButton1MainPanel);
		myMainPanel.add(myButton2MainPanel);
		myMainPanel.add(myButton3MainPanel);
		myMainPanel.add(myButton4MainPanel);
		myMainPanel.add(myButton5MainPanel);
		
		myClaButtonPanel.add(myClaResetButton);
		myAssButtonPanel.add(myAssResetButton);
		myAssClaButtonPanel.add(myAssClaResetButton);
		
		myClaPanel.add(myClaButtonPanel, BorderLayout.NORTH);
		myClaPanel.add(myClaTablePane, BorderLayout.CENTER);
		myAssPanel.add(myAssButtonPanel, BorderLayout.NORTH);
		myAssPanel.add(myAssTablePane, BorderLayout.CENTER);
		myAssClaPanel.add(myAssClaButtonPanel, BorderLayout.NORTH);
		myAssClaPanel.add(myAssClaTablePane, BorderLayout.CENTER);
		
		myTextArea = new JTextArea();
        myTextArea.setEditable(false);
        myLogWriter = new PrintWriter(new TextComponentWriter(myTextArea), true);
        myLogPane = new JScrollPane(myTextArea);
        myLogPane.setPreferredSize(new Dimension(249, 40));
        myLogPanel = new JPanel();
        myLogPanel.setLayout(new BorderLayout());
        myLogPanel.add(myLogPane, BorderLayout.CENTER);
        
        myTabPanel.addTab("Main", myMainPanel);
		myTabPanel.addTab("Classes", myClaPanel);
		myTabPanel.addTab("Associations", myAssPanel);
		myTabPanel.addTab("Associationclasses", myAssClaPanel);
		myTabPanel.addTab("Invariants", myOCLTablePane);
		myTabPanel.addTab("Outcome", myLogPane);
		
		this.add(myTabPanel, BorderLayout.CENTER);
	}
	
	private class MyColumn extends DefaultTableCellRenderer{
		private static final long serialVersionUID = 1L;
		
		private boolean cla;
		
		private MyColumn(boolean cl){
			cla = cl;
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable tabelle, Object value, boolean isSelected, boolean hasFocus, int row, int column){
	        setValue(value);
	        if(column == 0){
				setHorizontalAlignment(SwingConstants.LEFT);
			}else{
				setHorizontalAlignment(SwingConstants.CENTER);
			}
	        if(isSelected){
				setBackground(tabelle.getSelectionBackground());
				setForeground(tabelle.getSelectionForeground());
			}else{
				setBackground(tabelle.getBackground());
				setForeground(tabelle.getForeground());
			}
	        if(cla){
				UMLClass umlCla = (UMLClass) tabelle.getValueAt(row, -1);
				if(umlCla.getMClass().isAbstract()){
					setFont(new Font("SansSerif", Font.ITALIC, 11));
				}else{
					setFont(new Font("SansSerif", Font.PLAIN, 11));
				}
			}else{
				setFont(new Font("SansSerif", Font.PLAIN, 11));
			}
	        return this;
	    }   
		
	}	
	
	public void startKodkod(boolean indepen){
		
		if(checkIntegers()){
			if(myClaTable.isEditing()){
				myClaTable.getCellEditor().stopCellEditing();
			}
			if(myAssTable.isEditing()){
				myAssTable.getCellEditor().stopCellEditing();
			}
			if(myAssClaTable.isEditing()){
				myAssClaTable.getCellEditor().stopCellEditing();
			}
			if(!myClaTable.isEditing() && !myAssTable.isEditing() && !myAssClaTable.isEditing()){
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
				SetKodkodStruc setKodkod = new SetKodkodStruc(curMainWindow, curSystem, classes, associations, associationClasses, classInvariants, myLogWriter);
				if(UndefInfoInt!=null){
					curMainWindow.logWriter().println(UndefInfoInt);
				}
				if(UndefInfo!=null){
					curMainWindow.logWriter().println(UndefInfo);
				}
				setKodkod.runKodkod(indepen, solvNumber, true);
			}else{
				curMainWindow.logWriter().println("bounds must be integers");
			}
		}else{
			curMainWindow.logWriter().println("cannot run kodkod");
			curMainWindow.logWriter().println("attributes of type 'Integer' of existing objects");
			curMainWindow.logWriter().println("must be '> 0'");
		}
	}
	
	public void startSol(){
		
		SolverView sv = new SolverView(this);
		sv.setVisible(true);
		ViewFrame vf = new ViewFrame("select solver", sv, "");
		JComponent c = (JComponent) vf.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(new JScrollPane(sv), BorderLayout.CENTER);
		curMainWindow.addNewViewFrame(vf);
		
	}
	
	public void showNames(boolean ass){
		
		UMLClass umlCla;
		
		if(ass){
			umlCla = (UMLClass) myAssClaTable.getValueAt(myAssClaTable.getSelectedRow(), -1);
		}else{
			umlCla = (UMLClass) myClaTable.getValueAt(myClaTable.getSelectedRow(), -1);
		}
		
		if(umlCla.getMClass().allAttributes().size() > 0){
			AttNamesView bnv = new AttNamesView(umlCla, curMainWindow);
			bnv.setVisible(true);
			ViewFrame vf = new ViewFrame("Attribute values", bnv, "");
			JComponent c = (JComponent) vf.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(new JScrollPane(bnv), BorderLayout.CENTER);
			curMainWindow.addNewViewFrame(vf);
		}
		
	}
	
	private boolean checkIntegers(){
		
		int val = 0;
		
		for(int i = 0; i < myClaTable.getRowCount(); i++){
			UMLClass tmpUMLCla = (UMLClass) myClaTable.getValueAt(i, -1);
			for(int j = 0; j < tmpUMLCla.getMClass().allAttributes().size(); j++){
				MAttribute att = (MAttribute) tmpUMLCla.getMClass().allAttributes().get(j);
				if(att.type().isInteger()){
					for(int k = 0; k < tmpUMLCla.getObjectCount(); k++){
						MObject obj = (MObject) curSystem.state().objectsOfClass(tmpUMLCla.getMClass()).toArray()[k];
						if(obj.state(curSystem.state()).attributeValue(att).toString().equals("Undefined")){
							UndefInfoInt = "'undefined' attributes of type 'Integer' of existing objects are set to '1'";
						}else{
							val = new Integer(obj.state(curSystem.state()).attributeValue(att).toString()).intValue();
							if(val <= 0){
								return false;
							}
						}
					}
				}else{
					for(int k = 0; k < tmpUMLCla.getObjectCount(); k++){
						MObject obj = (MObject) curSystem.state().objectsOfClass(tmpUMLCla.getMClass()).toArray()[k];
						if(obj.state(curSystem.state()).attributeValue(att).toString().equals("Undefined")){
							UndefInfo = "'undefined' attributes of existing objects get other value";
						}
					}
				}
				
			}
		}
		
		for(int i = 0; i < myAssClaTable.getRowCount(); i++){
			UMLAssociationClass tmpUMLAssCla = (UMLAssociationClass) myAssClaTable.getValueAt(i, -1);
			for(int j = 0; j < tmpUMLAssCla.getMClass().allAttributes().size(); j++){
				MAttribute att = (MAttribute) tmpUMLAssCla.getMClass().allAttributes().get(j);
				if(att.type().isInteger()){
					for(int k = 0; k < tmpUMLAssCla.getObjectCount(); k++){
						MObject obj = (MObject) curSystem.state().objectsOfClass(tmpUMLAssCla.getMClass()).toArray()[k];
						if(obj.state(curSystem.state()).attributeValue(att).toString().equals("Undefined")){
							UndefInfoInt = "'undefined' attributes of type 'Integer' of existing objects are set to '1'";
						}else{
							val = new Integer(obj.state(curSystem.state()).attributeValue(att).toString()).intValue();
							if(val <= 0){
								return false;
							}
						}
					}
				}else{
					for(int k = 0; k < tmpUMLAssCla.getObjectCount(); k++){
						MObject obj = (MObject) curSystem.state().objectsOfClass(tmpUMLAssCla.getMClass()).toArray()[k];
						if(obj.state(curSystem.state()).attributeValue(att).toString().equals("Undefined")){
							UndefInfo = "'undefined' attributes of existing objects get other value";
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public void changeSolver(int num){
		solvNumber = num;
	}
	
	public int getSolver(){
		return solvNumber;
	}
	
	private void fitWidth(JTable tab){
		
		TableColumnModel colModel = tab.getColumnModel();
		
		for(int i = 0; i < colModel.getColumnCount(); i++){
			TableColumn col = colModel.getColumn(i);
			int width = 0;
			
			TableCellRenderer renderer = col.getHeaderRenderer();
			if(renderer == null){
				renderer = tab.getTableHeader().getDefaultRenderer();
			}
			
			Component comp = renderer.getTableCellRendererComponent(tab, col.getHeaderValue(), false, false, 0, 0);
			width = comp.getPreferredSize().width;
			
			for(int r = 0; r < tab.getRowCount(); r++){
				renderer = tab.getCellRenderer(r, i);
				comp = renderer.getTableCellRendererComponent(tab, tab.getValueAt(r, i), false, false, r, i);
				width = Math.max(width, comp.getPreferredSize().width);
			}
			if(i == 0){
				if(width < 101){
					width = 101;
				}
			}else{
				if(width < 62){
					width = 62;
				}
			}
			col.setPreferredWidth(width + 8);
		}
	}
	
	public void stateChanged(StateChangeEvent e){
		for(int i = 0; i < myClaTable.getRowCount(); i++){
			myClaTable.setValueAt(myClaTable.getValueAt(i, 1), i, 1);
			myClaTable.setValueAt(myClaTable.getValueAt(i, 2), i, 2);
		}
		for(int i = 0; i < myAssTable.getRowCount(); i++){
			myAssTable.setValueAt(myAssTable.getValueAt(i, 1), i, 1);
			myAssTable.setValueAt(myAssTable.getValueAt(i, 2), i, 2);
		}
		for(int i = 0; i < myAssClaTable.getRowCount(); i++){
			myAssClaTable.setValueAt(myAssClaTable.getValueAt(i, 1), i, 1);
			myAssClaTable.setValueAt(myAssClaTable.getValueAt(i, 2), i, 2);
		}
	}
	
	public void detachModel(){
		curSystem.removeChangeListener(this);
	}
	
	//JW ASSL-Window
	public void showAsslWindow(){
		AsslView bnv = new AsslView(curMainWindow, myClaTable, myAssTable, myAssClaTable, myOCLTable, curSystem, myLogWriter);
		bnv.setVisible(true);
		ViewFrame vf = new ViewFrame("ASSL", bnv, "");
		JComponent c = (JComponent) vf.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(new JScrollPane(bnv), BorderLayout.CENTER);
		curMainWindow.addNewViewFrame(vf);	
	}
}