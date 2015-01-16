package org.tzi.use.kodkod.plugin.gui.model;

import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationConversion;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsClass;

public class TableModelClass extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<SettingsClass> classesSettings = Collections.emptyList();
	
	private static String[] columnNames = new String[] {
			"Class",
			ConfigurationConversion.CLASSES_MIN,
			ConfigurationConversion.CLASSES_MAX,
			ConfigurationConversion.CLASSES_VALUES };
	
	public TableModelClass(List<SettingsClass> classesSettings) {
		super();
		if (classesSettings != null) {
			this.classesSettings = classesSettings;
		}
	}
	
	@Override
	public int getRowCount() {
		if (this.classesSettings != null) {
			return this.classesSettings.size(); 
		} else {
			return 0;
		}
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
		SettingsClass set = classesSettings.get(row);
		
		switch(col) {
		case 0: 
			return set.getCls().name();
		case 1:
			return set.getBounds().getLower();
		case 2:
			return set.getBounds().getUpper();
		case 3:
			return set.getValues();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		SettingsClass set = this.classesSettings.get(row);
		
		switch (column) {
		case 1:
			set.getBounds().setLower(aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			set.getBounds().setUpper(aValue);
			fireTableCellUpdated(row, column);
			break;
		case 3:
			set.setValues(aValue);
			fireTableCellUpdated(row, column);
			break;
		default:
			break;
		}
	}

	public List<SettingsClass> getClassesSettings() {
		return classesSettings;
	}

}