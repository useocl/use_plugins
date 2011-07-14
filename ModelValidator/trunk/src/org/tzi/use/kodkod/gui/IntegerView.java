package org.tzi.use.kodkod.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.tzi.use.gui.views.View;
import org.tzi.use.kodkod.main.UMLAttributeNames;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * IntegerView started from AttNamesView
 * shows min and max possible values of integer 
 * attributes of class or associationclass
 * @author  Torsten Humann
 */
public class IntegerView extends JPanel implements View{
	private static final long serialVersionUID = 1L;
	
	private final JLabel myLabel;
	private final JTable myIntegerTable;
	private final JScrollPane myIntegerTablePane;
	private final IntegerTableModel myIntegerTableModel;
	private final TableColumn[] column = new TableColumn[2];
	
	public IntegerView(UMLAttributeNames attNam, AttNamesTableModel aTM){
		super(new BorderLayout());
		
		myLabel = new JLabel(attNam.getAttributeName());
		myLabel.setPreferredSize(new Dimension(250, 50));
		myLabel.setHorizontalAlignment(JLabel.CENTER);
		myLabel.setVerticalAlignment(JLabel.CENTER);
		
		myIntegerTableModel = new IntegerTableModel(attNam, aTM);
		myIntegerTable = new JTable(myIntegerTableModel);
		myIntegerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myIntegerTable.setPreferredScrollableViewportSize(new Dimension(250, 95));
		myIntegerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myIntegerTablePane = new JScrollPane(myIntegerTable);
		for(int i = 0; i < column.length; i++){
			column[i] = myIntegerTable.getColumnModel().getColumn(i);
			column[i].setCellRenderer(new MyColumn());
		}
		fitWidth(myIntegerTable);
		myIntegerTable.getTableHeader().setReorderingAllowed(false);
		
		this.add(myLabel, BorderLayout.NORTH);
		this.add(myIntegerTablePane, BorderLayout.CENTER);
		
	}
	
	private class MyColumn extends DefaultTableCellRenderer{
		private static final long serialVersionUID = 1L;
		
		private MyColumn(){
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
	        setFont(new Font("SansSerif", Font.PLAIN, 11));
			return this;
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
				if(width < 163){
					width = 163;
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
		
	}
	
	public void detachModel(){
		
	}
	
}
