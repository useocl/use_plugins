package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.InvariantSettings;

public class TableModelInvariant extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<InvariantSettings> settings = new ArrayList<>();
	
	private static String[] columnNames = new String[] {
		ConfigurationTerms.INVARIANTS, 
		ConfigurationTerms.INVARIANTS_ACTIVE, 
		ConfigurationTerms.INVARIANTS_NEGATE 
	};
	
	public TableModelInvariant(List<InvariantSettings> settings) {
		super();
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
		return 3;
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
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
		case 0: 
			return settings.get(row).getInvariant().cls().name() + "::" + settings.get(row).getInvariant().name();
		case 1:
			if (settings.get(row).getActive() != null) {
				return settings.get(row).getActive();
			} else {
				return DefaultConfigurationValues.INVARIANT_ACTIVE;
			}
		case 2:
			if (settings.get(row).getNegate() != null) {
				return settings.get(row).getNegate();
			} else {
				return DefaultConfigurationValues.INVARIANT_NEGATE;
			}
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
        return getValueAt(0, col).getClass();
    }

}
