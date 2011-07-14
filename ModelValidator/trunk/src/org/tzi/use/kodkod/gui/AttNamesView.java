package org.tzi.use.kodkod.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.views.View;
import org.tzi.use.kodkod.main.UMLAttributeNames;
import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * AttNamesView started from KodkodView
 * shows attributes with possible values 
 * of class or associationclass
 * @author  Torsten Humann
 */
public class AttNamesView extends JPanel implements View{
	private static final long serialVersionUID = 1L;
	
	private MainWindow curMainWindow;
	private UMLClass umlCla;
	private final JTable myAttNamTable;
	private final JScrollPane myAttNamTablePane;
	private final AttNamesTableModel myAttNamTableModel;
	private final JPanel myButtonPanel;
	private final JButton myApplyButton;
	private final JButton myResetButton;
	
	public AttNamesView(UMLClass cla, MainWindow win){
		super(new BorderLayout());
		
		curMainWindow = win;
		umlCla = cla;
		
		myAttNamTableModel = new AttNamesTableModel(umlCla);
		myAttNamTable = new JTable(myAttNamTableModel);
		myAttNamTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myAttNamTable.setPreferredScrollableViewportSize(new Dimension(250, 95));
		myAttNamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myAttNamTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2 && myAttNamTable.getSelectedColumn() == 1){
					showInteger();
				}
			}
		});
		myAttNamTablePane = new JScrollPane(myAttNamTable);
		fitWidth(myAttNamTable);
		
		myButtonPanel = new JPanel();
		myButtonPanel.setLayout(new FlowLayout());
		
		myApplyButton = new JButton();
		myApplyButton.setText("apply");
		myApplyButton.setPreferredSize(new Dimension(100, 25));
		myApplyButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				applyChanges();
			}
		});
		
		myResetButton = new JButton();
		myResetButton.setText("reset values");
		myResetButton.setPreferredSize(new Dimension(100, 25));
		myResetButton.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				if(myAttNamTable.isEditing()){
					myAttNamTable.getCellEditor().stopCellEditing();
				}
				myAttNamTableModel.initModel();
			}
		});
		
		myButtonPanel.add(myApplyButton);
		myButtonPanel.add(myResetButton);
		
		this.add(myAttNamTablePane, BorderLayout.NORTH);
		this.add(myButtonPanel, BorderLayout.CENTER);
		
	}
	
	public void applyChanges() {
		if(myAttNamTable.isEditing()){
			myAttNamTable.getCellEditor().stopCellEditing();
		}
		for(int i = 0; i < umlCla.getMClass().allAttributes().size(); i++){
			if(umlCla.isAttributeEditable(myAttNamTable.getValueAt(i, 0).toString())){
				if(!myAttNamTable.getValueAt(i, 1).toString().equals("")){
					String[] splitArray = myAttNamTable.getValueAt(i, 1).toString().split(",");
					for(int j = 0; j < splitArray.length; j++){
						splitArray[j] = splitArray[j].trim();
					}
					umlCla.setBoundNames(myAttNamTable.getValueAt(i, 0).toString(), new ArrayList<String>(Arrays.asList(splitArray)));
				}else{
					umlCla.setBoundNames(myAttNamTable.getValueAt(i, 0).toString(), new ArrayList<String>());
				}
			}
		}
	}
	
	private void showInteger(){
		UMLAttributeNames att = (UMLAttributeNames) myAttNamTable.getValueAt(myAttNamTable.getSelectedRow(), -1);
		
		if(att.isInteger()){
			IntegerView iv = new IntegerView(att, myAttNamTableModel);
			iv.setVisible(true);
			ViewFrame vf = new ViewFrame("Integer values", iv, "");
			JComponent c = (JComponent) vf.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(new JScrollPane(iv), BorderLayout.CENTER);
			curMainWindow.addNewViewFrame(vf);
		}
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
				if(width < 121){
					width = 121;
				}
			}else{
				if(width < 147){
					width = 147;
				}
			}
			col.setPreferredWidth(width + 8);
		}
	}
	
	public void stateChanged(StateChangeEvent e){
		
	}
	
	public void detachModel(){
		
	}
	
}
