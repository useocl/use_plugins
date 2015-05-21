package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.AssociationSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.util.StringUtil;

public class TableModelAssociation extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<AssociationSettings> associationsSettings = Collections.emptyList();

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.ASSOCIATIONS,
		ConfigurationTerms.ASSOCIATIONS_MIN,
		ConfigurationTerms.ASSOCIATIONS_MAX,
		ConfigurationTerms.ASSOCIATIONS_VALUES
	};

	public TableModelAssociation(List<AssociationSettings> settings) {
		associationsSettings = settings;
	}

	@Override
	public int getRowCount() {
		return associationsSettings.size();
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
		return column > 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AssociationSettings set = associationsSettings.get(row);

		switch(col) {
		case 0:
			return set.getAssociation().name();
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
		AssociationSettings set = associationsSettings.get(row);

		switch (column) {
		case 1:
			set.setLowerBound((Integer) aValue);
			break;
		case 2:
			set.setUpperBound((Integer) aValue);
			break;
		case 3:
			String[] split = ((String) aValue).split(",");
			Set<String> list = new LinkedHashSet<String>();
			for (int i = 0; i < split.length; i++) {
				list.add(split[i].trim());
			}
			set.setInstanceNames(list);
			break;
		}
	}

	public void setClass(ClassSettings classSettings) {
		associationsSettings = new ArrayList<>(classSettings.getAssociationSettings().values());
		fireTableDataChanged();
	}

	public List<AssociationSettings> getAssociationsSettings() {
		return associationsSettings;
	}

}
