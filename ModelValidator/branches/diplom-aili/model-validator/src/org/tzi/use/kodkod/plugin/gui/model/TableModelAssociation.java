package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.AssociationSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.util.StringUtil;

public class TableModelAssociation extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<AssociationSettings> associationsSettings = Collections.emptyList();
	
	private static String[] columnNames = new String[] {
			ConfigurationTerms.ASSOCIATIONS,
			ConfigurationTerms.ASSOCIATIONS_MIN,
			ConfigurationTerms.ASSOCIATIONS_MAX,
			ConfigurationTerms.ASSOCIATIONS_VALUES };
	
	public TableModelAssociation(List<AssociationSettings> settings) {
		super();
		if (settings != null) {
			this.associationsSettings = settings;
		}
	}

	@Override
	public int getRowCount() {
		if (associationsSettings != null) {
			return this.associationsSettings.size();
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
		AssociationSettings set = associationsSettings.get(row);
		
		switch(col) {
		case 0: 
			return set.getAssociation().name();
		case 1:
			if (set.getBounds().getLower() != null) {
				return set.getBounds().getLower();
			} else {
				return "";
			}
		case 2:
			if (set.getBounds().getUpper() != null) {
				return set.getBounds().getUpper();
			} else {
				return "";
			}
		case 3:
			return StringUtil.fmtSeq(set.getValues(), ",");
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		AssociationSettings set = this.associationsSettings.get(row);
		
		switch (column) {
		case 1:
			set.getBounds().setLower(aValue);
			break;
		case 2:
			set.getBounds().setUpper(aValue);
			break;
		case 3:
			set.setValues((String) aValue);
			break;
		default:
			break;
		}
	}

	public void setClass(ClassSettings classSettings) {
		this.associationsSettings = new ArrayList<>(classSettings.getAssociationSettings().values());
		this.fireTableDataChanged();
	}

	public List<AssociationSettings> getAssociationsSettings() {
		return associationsSettings;
	}

}
