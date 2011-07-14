package org.tzi.use.kodkod.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.main.UMLClass;
import org.tzi.use.kodkod.main.UMLClassImpl;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MSystem;

/**
 * TableModel for classes in KodkodView
 * @author  Torsten Humann
 */
public class ClaTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private MSystem curSystem;
	private Collection<?> mClasses;
	private ArrayList<String> fColumnNames;
	private ArrayList<UMLClass> fRows;
	
	public ClaTableModel(MSystem curSys){
		fRows = new ArrayList<UMLClass>();
		fColumnNames = new ArrayList<String>();
		curSystem = curSys;
		mClasses = curSystem.model().classes();
		initStructure();
		initModel();
	}
	
	private void initStructure(){
		fColumnNames.clear();
		if(mClasses == null){
			return;
		}
		fColumnNames.add("class");
		fColumnNames.add("lower bound");
		fColumnNames.add("upper bound");
	}
	
	private void initModel(){
		fRows.clear();
		if(mClasses == null){
			return;
		}
		for(int i = 0; i < mClasses.size(); i++){
			MClass cla = (MClass) mClasses.toArray()[i];
			if(curSystem.model().getAssociation(cla.name()) == null){
				fRows.add(new UMLClassImpl(curSystem, cla));
			}
		}
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
	 		return !fRows.get(row).getMClass().isAbstract();
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
			case 0:	return fRows.get(row).getTableName();
			case 1: return fRows.get(row).getLowerBound();
			case 2: return fRows.get(row).getUpperBound();
			case -1: return fRows.get(row);
			case -2: return fRows.get(row).getName();
			default:	return "";
		}	
	}
	
	@Override
	//sets min and max count of objects for classes
	//min not lower than objectcount of class
	//max not lower than min
	public void setValueAt(Object value, int row, int col){
		if(value == null){
			value = 0;
		}
		
		UMLClass tmpRow = new UMLClassImpl(curSystem, fRows.get(row).getMClass());
		tmpRow.setUMLAttributeNames(fRows.get(row).getUMLAttributeNames());
		if(col == 1){
			if(fRows.get(row).getObjectCount() > (Integer)value){
				tmpRow.setLowerBound(fRows.get(row).getObjectCount());
				if(fRows.get(row).getObjectCount() > fRows.get(row).getUpperBound()){
					tmpRow.setUpperBound(fRows.get(row).getObjectCount());
				}else{
					tmpRow.setUpperBound(fRows.get(row).getUpperBound());
				}
			}else{
				tmpRow.setLowerBound((Integer)value);
				if(fRows.get(row).getUpperBound() > (Integer)value){
					tmpRow.setUpperBound(fRows.get(row).getUpperBound());
				}else{
					tmpRow.setUpperBound((Integer)value);
				}
			}
		}else{
			if(fRows.get(row).getObjectCount() > (Integer)value){
				tmpRow.setLowerBound(fRows.get(row).getObjectCount());
				tmpRow.setUpperBound(fRows.get(row).getObjectCount());
			}else{
				tmpRow.setUpperBound((Integer)value);
				if(fRows.get(row).getLowerBound() > (Integer)value){
					tmpRow.setLowerBound((Integer)value);
				}else{
					tmpRow.setLowerBound(fRows.get(row).getLowerBound());
				}
			}
		}
		fRows.set(row, tmpRow);
		fireTableCellUpdated(row,1);
		fireTableCellUpdated(row,2);
	}
	
}