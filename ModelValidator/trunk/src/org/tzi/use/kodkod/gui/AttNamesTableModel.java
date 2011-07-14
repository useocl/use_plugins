package org.tzi.use.kodkod.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.main.UMLAttributeNames;
import org.tzi.use.kodkod.main.UMLClass;

/**
 * TableModel for attributes in AttNamesView
 * @author  Torsten Humann
 */
public class AttNamesTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private UMLClass umlCla;
	private ArrayList<String> fColumnNames;
	private ArrayList<UMLAttributeNames> fRows;
	private String[] fValues;
	
	public AttNamesTableModel(UMLClass cla){
		umlCla = cla;
		fColumnNames = new ArrayList<String>();
		fRows = umlCla.getUMLAttributeNames();
		fColumnNames.add("attribute");
		fColumnNames.add("posible values");
		initModel();
	}
	
	public void initModel(){
		final int n = fRows.size();
		fValues = new String[n];
		for(int i = 0; i < n; i++){
			fValues[i] = "";
			if(fRows.get(i).isENum()){
				for(int j = 0; j < fRows.get(i).eNums().size(); j++){
					if(j == 0){
						fValues[i] = fRows.get(i).eNums().get(j);
					}else{
						fValues[i] = fValues[i] + ", " + fRows.get(i).eNums().get(j);
					}
				}
			}else if(fRows.get(i).isObject()){
				fValues[i] = "all Objects of type " + fRows.get(i).objectTypeName();
			}else if(fRows.get(i).isInteger()){
				if(fRows.get(i).getMinValue()==fRows.get(i).getMaxValue()){
					fValues[i] = Integer.toString(fRows.get(i).getMinValue());
				}else{
					fValues[i] = fRows.get(i).getMinValue() + ".." + fRows.get(i).getMaxValue();
				}
			}else{
				for(int j = 0; j < fRows.get(i).getBoundNames().size(); j++){	
					if(j == 0){
						fValues[i] = fRows.get(i).getBoundNames().get(j);
					}else{
						fValues[i] = fValues[i] + ", " + fRows.get(i).getBoundNames().get(j);
					}
				}
			}
		}
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
	 		return fRows.get(row).isEditable();
	 	}
	}
	
	@Override
	public Class<?> getColumnClass(int col){
		return String.class;
	}
	
	@Override
	public Object getValueAt(int row, int col){
		switch(col){
			case 0:	return fRows.get(row).getAttributeName();
			case 1: return fValues[row];
			case -1: return fRows.get(row);
			default:	return "";
		}	
	}
	
	@Override
	public void setValueAt(Object value, int row, int col){
		fValues[row] = value.toString();
        fireTableCellUpdated(row, col);
	}

}