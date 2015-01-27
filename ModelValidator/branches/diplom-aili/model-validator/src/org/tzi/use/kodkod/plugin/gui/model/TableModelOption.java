package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsOption;

public class TableModelOption extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private SettingsOption settings;
	
	private static String[] columnNames = new String[] {
		ConfigurationTerms.OPTIONS,
		"" };
	
	public TableModelOption(SettingsOption settings) {
		this.settings = settings;
	}
	
	@Override
	public int getRowCount() {
		return 2; 
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
		case 0: 
			switch(row) {
			case 0:
				return ConfigurationTerms.OPTION_AGGREGATIONCYCLEFREENESS;
			case 1:
				return ConfigurationTerms.OPTION_FORBIDDENSHARING;
			}
		case 1:
			switch(row) {
			case 0:
				return settings.getAggregationcyclefreeness();
			case 1:
				return settings.getForbiddensharing();
			}
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 1) {
			if (row == 0) {
				settings.setAggregationcyclefreeness((Boolean) value);
			} else if (row == 1) {
				settings.setForbiddensharing((Boolean) value);
			}
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

	public SettingsOption getSettings() {
		return settings;
	}

}
