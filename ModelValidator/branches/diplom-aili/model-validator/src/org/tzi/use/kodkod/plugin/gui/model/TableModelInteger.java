package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsInteger;
import org.tzi.use.util.StringUtil;

public class TableModelInteger extends DefaultTableModel{
	private static final long serialVersionUID = 1L;
	
	private SettingsInteger settings;
	
	private static String[] columnNames = new String[] {
		ConfigurationTerms.INTEGER_MIN,
		ConfigurationTerms.INTEGER_MAX,
		ConfigurationTerms.INTEGER_VALUES };
	
	public TableModelInteger(SettingsInteger set){
		super();
		this.settings = set;
	}
	
	@Override
	public int getRowCount() {
		return 1; 
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
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
			if (settings.getMinimum() != null) {
				return settings.getMinimum();
			} else {
				return "";
			}
		case 1:
			if (settings.getMaximum() != null) {
				return settings.getMaximum();
			} else {
				return "";
			}
		case 2:
			return StringUtil.fmtSeq(settings.getValues(), ",");
		}
		
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 1:
			settings.setMinimum(aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			settings.setMaximum(aValue);
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

	public SettingsInteger getSettings() {
		return settings;
	}

}
