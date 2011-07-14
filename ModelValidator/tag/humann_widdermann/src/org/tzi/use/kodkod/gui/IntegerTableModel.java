package org.tzi.use.kodkod.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.main.UMLAttributeNames;

/**
 * TableModel for integer values in IntegerView
 * @author  Torsten Humann
 */
public class IntegerTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private UMLAttributeNames attNam;
	private AttNamesTableModel attTabMod;
	private ArrayList<String> fColumnNames;
	private ArrayList<String> fRows;
	
	public IntegerTableModel(UMLAttributeNames aNam, AttNamesTableModel aTM){
		attNam = aNam;
		attTabMod = aTM;
		fColumnNames = new ArrayList<String>();
		fColumnNames.add("");
		fColumnNames.add("value");
		fRows = new ArrayList<String>();
		fRows.add("minimum");
		fRows.add("maximum");
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
			return Integer.class;
		}
	}
	
	
	@Override
	public Object getValueAt(int row, int col){
		switch(col){
			case 0:	return fRows.get(row);
			case 1: 
				switch(row){
					case 0: return attNam.getMinValue();
					case 1: return attNam.getMaxValue();
				}
			default:	return "";
		}	
	}
	
	@Override
	//sets min and max value for integer attribute
	//min > 0, max >= min
	public void setValueAt(Object value, int row, int col){
		if(value == null){
			value = 0;
		}
		
		if(row==0){
			if((Integer)value > attNam.getMaxValue()){
				attNam.setMinValue(attNam.getMaxValue());
			}else if((Integer)value < 1){
				attNam.setMinValue(1);
			}else{
				attNam.setMinValue((Integer)value);
			}
		}else{
			if((Integer)value < attNam.getMinValue()){
				attNam.setMaxValue(attNam.getMinValue());
			}else{
				attNam.setMaxValue((Integer)value);
			}
		}
		attTabMod.initModel();
		fireTableDataChanged();
	}

}