package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.StringSettings;
import org.tzi.use.util.StringUtil;

public class TableModelString extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private StringSettings settings;
	
	private static String[] columnNames = new String[] {
		ConfigurationTerms.BASIC_TYPE,
		ConfigurationTerms.STRING_MIN,
		ConfigurationTerms.STRING_MAX,
		ConfigurationTerms.STRING_VALUES };
	
	public TableModelString(StringSettings settings) {
		super();
		this.settings = settings;
	}
	
	@Override
	public int getRowCount() {
		return 1; 
	}

	@Override
	public int getColumnCount() {
		return 4;
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
			if (settings.getBounds().getLower() != null) {
				return settings.getBounds().getLower();
			} else {
				return "";
			}
		case 2:
			if (settings.getBounds().getUpper() != null) {
				return settings.getBounds().getUpper();
			} else {
				return "";
			}
		case 3:
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
			settings.setValues((String) aValue);
			fireTableCellUpdated(row, column);
			break;
		default:
			break;
		}
	}

	public StringSettings getSettings() {
		return settings;
	}

}
