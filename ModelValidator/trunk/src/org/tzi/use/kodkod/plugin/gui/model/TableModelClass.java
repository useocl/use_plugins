package org.tzi.use.kodkod.plugin.gui.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.util.StringUtil;

public class TableModelClass extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final List<ClassSettings> classesSettings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.CLASSES,
		ConfigurationTerms.CLASSES_MIN,
		ConfigurationTerms.CLASSES_MAX,
		ConfigurationTerms.CLASSES_VALUES };

	public TableModelClass(List<ClassSettings> classesSettings) {
		this.classesSettings = classesSettings;
	}

	@Override
	public int getRowCount() {
		return classesSettings.size();
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
		if ((classesSettings.get(row).getCls() instanceof IAssociationClass) && (column == 1 || column == 2)) {
			// association class bounds are setup via the association table
			return false;
		} else if (classesSettings.get(row).getCls().isAbstract()) {
			return false;
		} else {
			return column > 0;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		ClassSettings set = classesSettings.get(row);

		switch(col) {
		case 0:
			return set.getCls().name();
		case 1:
			return set.getLowerBound();
		case 2:
			return set.getUpperBound();
		case 3:
			return StringUtil.fmtSeq(set.getInstanceNames(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		ClassSettings set = classesSettings.get(row);

		switch (column) {
		case 1:
			set.setLowerBound((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			set.setUpperBound((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 3:
			String[] split = ((String) aValue).split(",");
			Set<String> list = new LinkedHashSet<String>();
			for (int i = 0; i < split.length; i++) {
				list.add(split[i].trim());
			}
			set.setInstanceNames(list);
			fireTableCellUpdated(row, column);
			break;
		}
	}

	public List<ClassSettings> getClassesSettings() {
		return classesSettings;
	}

}
