package org.tzi.use.kodkod.gui;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.gen.assl.statics.GProcedure;

/**
 * TableModel for parameters in AsslProcedure
 * @author  Juergen Widdermann 
 */
public class AsslParameterTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	
	private final GProcedure procedure;

	private String[] fValues;
	
	public AsslParameterTableModel(GProcedure procedure){
		this.procedure = procedure; 
		initModel();
	}
	
	public void initModel(){
		final int n = procedure.parameterDecls().size();
		fValues = new String[n];
		for(int i = 0; i < n; i++){
			fValues[i] = "";
		}
		fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount(){
		return 2;
	}
	
	@Override
	public int getRowCount(){
		return procedure.parameterDecls().size();
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
		return String.class;
	}
	
	@Override
	public Object getValueAt(int row, int col){
		switch(col){
			case 0:	return procedure.parameterDecls().get(row);
			case 1: return fValues[row];
			default:	return "";
		}	
	}
	
	@Override
	public void setValueAt(Object value, int row, int col){
		fValues[row] = value.toString();
        fireTableCellUpdated(row, col);
	}

}