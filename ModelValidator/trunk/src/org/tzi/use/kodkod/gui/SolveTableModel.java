package org.tzi.use.kodkod.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * TableModel for possible SAT-Solver
 * @author  Torsten Humann
 */
public class SolveTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private int solvNumber;
	private KodkodView kodView;
	private ArrayList<String> fColumnNames;
	private ArrayList<String> fRows;
	
	public SolveTableModel(KodkodView kv){
		fColumnNames = new ArrayList<String>();
		fColumnNames.add("solver");
		fColumnNames.add("");
		fRows = new ArrayList<String>();
		fRows.add("DefaultSAT4J");
		fRows.add("LightSAT4J");
		fRows.add("MiniSat");
		fRows.add("MiniSatProver");
		fRows.add("ZChaff");
		fRows.add("ZChaffMincost");
		solvNumber = kv.getSolver();
		kodView = kv;
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int col){
		return (String) fColumnNames.get(col);
	}
	
	@Override
	public int getColumnCount(){
		return fColumnNames.size();
	}
	
	@Override
	public int getRowCount(){
		return fRows.size();
	}
	
	@Override
	public boolean isCellEditable(int row, int col){
	 	if(col == 0){
	 		return false;
	 	}else{
	 		return true;
	 	}
	}
	
	@Override
	public Class<?> getColumnClass(int col){
		if(col == 0){
			return String.class;
		}else{
			return Boolean.class;
		}
	}
	
	
	@Override
	public Object getValueAt(int row, int col){
		solvNumber = kodView.getSolver();
		switch(col){
			case 0:	return fRows.get(row);
			case 1: 
				if(row == solvNumber){
					return true;
				}else{
					return false;
				}
			default:	return "";
		}	
	}
	
	@Override
	//sets the SAT-Solver
	public void setValueAt(Object value, int row, int col){
		if((Boolean) value){
			kodView.changeSolver(row);
		}
		fireTableDataChanged();
	}

}