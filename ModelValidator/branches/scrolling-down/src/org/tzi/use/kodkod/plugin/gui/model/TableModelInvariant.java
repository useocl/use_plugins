package org.tzi.use.kodkod.plugin.gui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.InvariantSettings;

public class TableModelInvariant extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final List<InvariantSettings> settings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.INVARIANTS,
		ConfigurationTerms.INVARIANTS_ACTIVE,
		ConfigurationTerms.INVARIANTS_NEGATE
	};
	
	public TableModelInvariant(List<InvariantSettings> settings) {
		this.settings = settings;
	}

	@Override
	public int getRowCount() {
		if (settings != null) {
			return settings.size();
		} else {
			return 0;
		}
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column >= 1;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
		case 0:
			return settings.get(row).getInvariant().clazz().name() + "::" + settings.get(row).getInvariant().name();
		case 1:
			return settings.get(row).isActive();
		case 2:
			return settings.get(row).isNegate();
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		switch(col) {
		case 1:
			settings.get(row).setActive((Boolean) value);
			fireTableCellUpdated(row, col);
			break;
		case 2:
			settings.get(row).setNegate((Boolean) value);
			fireTableCellUpdated(row, col);
			break;
		}
	}

	public List<InvariantSettings> getSettings() {
		return settings;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if(col > 0){
			return Boolean.class;
		} else {
			return super.getColumnClass(col);
		}
	}

}
