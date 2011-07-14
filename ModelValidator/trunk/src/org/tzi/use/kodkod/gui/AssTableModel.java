package org.tzi.use.kodkod.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.main.UMLAssociation;
import org.tzi.use.kodkod.main.UMLAssociationImpl;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.sys.MSystem;

/**
 * TableModel for associations in KodkodView
 * @author  Torsten Humann
 */
public class AssTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private MSystem curSystem;
	private Collection<?> mAssociations;
	private ArrayList<String> fColumnNames;
	private ArrayList<UMLAssociation> fRows;
	
	public AssTableModel(MSystem curSys){
		fRows = new ArrayList<UMLAssociation>();
		fColumnNames = new ArrayList<String>();
		curSystem = curSys;
		mAssociations = curSystem.model().associations();
		initStructure();
		initModel();
	}
	
	private void initStructure(){
		fColumnNames.clear();
		if(mAssociations == null){
			return;
		}
		fColumnNames.add("association");
		fColumnNames.add("lower bound");
		fColumnNames.add("upper bound");
	}
	
	private void initModel(){
		fRows.clear();
		if(mAssociations == null){
			return;
		}
		for(int i = 0; i < mAssociations.size(); i++){
			MAssociation ass = (MAssociation) mAssociations.toArray()[i];
			if(curSystem.model().getClass(ass.name()) == null){
				fRows.add(new UMLAssociationImpl(curSystem, ass));
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
			case 0:	return fRows.get(row).getName();
			case 1: return fRows.get(row).getLowerBound();
			case 2: return fRows.get(row).getUpperBound();
			case -1: return fRows.get(row);
			case -2: return fRows.get(row).getName();
			default:	return "";
		}	
	}
	
	@Override
	//sets min and max count of links for associations
	//min not lower than linkcount of association
	//max not lower than min
	public void setValueAt(Object value, int row, int col){
		if(value == null){
			value = 0;
		}
		UMLAssociation tmpRow = new UMLAssociationImpl(curSystem, fRows.get(row).getMAssociation());
		if(col == 1){
			if(fRows.get(row).getLinkCount() > (Integer)value){
				tmpRow.setLowerBound(fRows.get(row).getLinkCount());
				if(fRows.get(row).getLinkCount() > fRows.get(row).getUpperBound()){
					tmpRow.setUpperBound(fRows.get(row).getLinkCount());
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
			// special case: upper Bound = -1
			// this value present an Unlimitted
			if(fRows.get(row).getLinkCount() > (Integer)value && (Integer)value != -1){
				tmpRow.setLowerBound(fRows.get(row).getLinkCount());
				tmpRow.setUpperBound(fRows.get(row).getLinkCount());
			}else{
				tmpRow.setUpperBound((Integer)value);
				if(fRows.get(row).getLowerBound() > (Integer)value && (Integer)value != -1){
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