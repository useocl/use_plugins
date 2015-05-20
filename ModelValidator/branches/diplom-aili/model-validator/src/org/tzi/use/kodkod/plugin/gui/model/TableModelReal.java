package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsReal;
import org.tzi.use.util.StringUtil;

public class TableModelReal extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private SettingsReal settings;
	
	private static String[] columnNames = new String[] {
		ConfigurationTerms.BASIC_TYPE,
		ConfigurationTerms.REAL_MIN,
		ConfigurationTerms.REAL_MAX,
		ConfigurationTerms.REAL_STEP,
		ConfigurationTerms.REAL_VALUES };
	
	public TableModelReal(SettingsReal settings) {
		super();
		this.settings = settings;
	}
	
	@Override
	public int getRowCount() {
		return 1; 
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
		case 0: 
			return settings.name();
		case 1:
			if (settings.getMinimum() != null) {
				return settings.getMinimum();
			} else {
				return "";
			}
		case 2:
			if (settings.getMaximum() != null) {
				return settings.getMaximum();
			} else {
				return "";
			}
		case 3:
			if (settings.getStep() != null) {
				return settings.getStep();
			} else {
				return "";
			}
		case 4:
			return StringUtil.fmtSeq(settings.getValues(), ",");
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 1:
			if(aValue == null || aValue.equals("")){
				settings.setMinimum(null);
			} else {
				settings.setMinimum((Double) aValue);
			}
			fireTableCellUpdated(row, column);
			break;
		case 2:
			if(aValue == null || aValue.equals("")){
				settings.setMaximum(null);
			} else {
				settings.setMaximum((Double) aValue);
			}
			fireTableCellUpdated(row, column);
			break;
		case 3:
			if(aValue == null){
				settings.setStep(null);
			} else {
				settings.setStep((Double) aValue);
			}
			fireTableCellUpdated(row, column);
			break;
		case 4:
			settings.setValues((String) aValue);
			fireTableCellUpdated(row, column);
			break;
		default:
			break;
		}
	}

	public SettingsReal getSettings() {
		return settings;
	}

}
