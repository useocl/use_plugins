package org.tzi.use.kodkod.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.main.OCLInvar;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.sys.MSystem;

/**
 * TableModel for invariants in KodkodView
 * @author  Torsten Humann
 */
public class OCLTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private MSystem curSystem;
	private Collection<?> mInvs;
	private ArrayList<String> fColumnNames;
	private ArrayList<OCLInvar> fRows;
	
	public OCLTableModel(MSystem curSys){
		fRows = new ArrayList<OCLInvar>();
		fColumnNames = new ArrayList<String>();
		curSystem = curSys;
		mInvs = curSystem.model().classInvariants();
		initStructure();
		initModel();
	}
	
	private void initStructure(){
		fColumnNames.clear();
		if(mInvs == null){
			return;
		}
		fColumnNames.add("constraint");
		fColumnNames.add("positive");
		fColumnNames.add("negate");
		fColumnNames.add("deactivate");
	}
	
	private void initModel(){
		fRows.clear();
		if(mInvs == null){
			return;
		}
		for(int i = 0; i < mInvs.size(); i++){
			MClassInvariant inv = (MClassInvariant) mInvs.toArray()[i];
			fRows.add(new OCLInvar(inv));
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
			return Boolean.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col){
		switch(col){
			case 0: return fRows.get(row).getName();
			case 1: return (fRows.get(row).getFlag() == OCLInvar.Flag.p);
			case 2: return (fRows.get(row).getFlag() == OCLInvar.Flag.n);
			case 3: return (fRows.get(row).getFlag() == OCLInvar.Flag.d);
			case -1: return fRows.get(row);
			default:	return "";
		}	
	}
	
	@Override
	//sets invariant positive, negative or deactivated
	public void setValueAt(Object value, int row, int col){
		
		if(value == null){
			value = 0;
		}
		
		OCLInvar tmpRow = new OCLInvar(fRows.get(row).getMClassInvariant());
		if((Boolean) value){
			switch(col){
			case 1: 
				tmpRow.setFlag(OCLInvar.Flag.p);
				break;
			case 2: 
				tmpRow.setFlag(OCLInvar.Flag.n);
				break;
			case 3: 
				tmpRow.setFlag(OCLInvar.Flag.d);
				break;
			}
		}else{
			tmpRow.setFlag(fRows.get(row).getFlag());
		}
		fRows.set(row, tmpRow);
		fireTableCellUpdated(row,1);
		fireTableCellUpdated(row,2);
		fireTableCellUpdated(row,3);
	}
	
}