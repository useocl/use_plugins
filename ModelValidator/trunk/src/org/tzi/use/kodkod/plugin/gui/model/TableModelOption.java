package org.tzi.use.kodkod.plugin.gui.model;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.OptionSettings;

public class TableModelOption extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final OptionSettings settings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.OPTIONS,
		"Enabled"
	};

	public TableModelOption(OptionSettings settings) {
		this.settings = settings;
	}

	@Override
	public int getRowCount() {
		return 2;
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 1;
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
		}
		return null;
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

	public OptionSettings getSettings() {
		return settings;
	}

}
