package org.tzi.use.kodkod.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.tzi.use.gui.views.View;
import org.tzi.use.uml.sys.StateChangeEvent;

/**
 * SolverView started from KodkodView
 * shows possible SAT-Solver
 * @author  Torsten Humann
 */
public class SolverView extends JPanel implements View{
	private static final long serialVersionUID = 1L;
	
	private final JTable mySolveTable;
	private final JScrollPane mySolveTablePane;
	private final SolveTableModel mySolveTableModel;
	
	public SolverView(KodkodView kv){
		super(new BorderLayout());
		
		mySolveTableModel = new SolveTableModel(kv);
		mySolveTable = new JTable(mySolveTableModel);
		mySolveTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		mySolveTable.setPreferredScrollableViewportSize(new Dimension(250, 95));
		mySolveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mySolveTablePane = new JScrollPane(mySolveTable);
		fitWidth(mySolveTable);
		mySolveTable.getTableHeader().setReorderingAllowed(false);
		
		this.add(mySolveTablePane, BorderLayout.CENTER);
		
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
