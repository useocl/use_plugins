package org.tzi.use.kodkod.plugin.gui.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.StringSettings;
import org.tzi.use.util.StringUtil;

public class TableModelString extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final StringSettings settings;

	private static final String[] COLUMNS = new String[] {
		ConfigurationTerms.STRING_MIN,
		ConfigurationTerms.STRING_MAX,
		ConfigurationTerms.STRING_VALUES
	};

	public TableModelString(StringSettings settings) {
		this.settings = settings;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return settings.getLowerBound();
		case 1:
			return settings.getUpperBound();
		case 2:
			return StringUtil.fmtSeq(settings.getInstanceNames(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setLowerBound((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 1:
			settings.setUpperBound((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			String[] split = ((String) aValue).split(",");
			Set<String> list = new LinkedHashSet<String>();
			for (int i = 0; i < split.length; i++) {
				list.add(split[i].trim());
			}
			settings.setInstanceNames(list);
			fireTableCellUpdated(row, column);
			break;
		}
	}

	public StringSettings getSettings() {
		return settings;
	}

}
