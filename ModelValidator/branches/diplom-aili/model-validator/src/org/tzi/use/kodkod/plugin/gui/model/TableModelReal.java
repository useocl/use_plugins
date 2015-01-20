package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsReal;
import org.tzi.use.util.StringUtil;

public class TableModelReal extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private SettingsReal settings = new SettingsReal();
	
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
			return settings.getBounds().getLower();
		case 2:
			return settings.getBounds().getUpper();
		case 3:
			return settings.getStep();
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
			settings.getBounds().setLower(aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			settings.getBounds().setUpper(aValue);
			fireTableCellUpdated(row, column);
			break;
		case 3:
			settings.setStep(aValue);
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
