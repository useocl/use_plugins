package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsClass;
import org.tzi.use.util.StringUtil;

public class TableModelAttribute extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	//TODO: In der Attributentabelle sollen die Spalten fuer MinDefined, MaxDefined, 
	//MinElements, MaxElements standardmaessig weggeblendet sein, mit einer Checkbox, um sie wieder einzublenden.
	//Min-/MaxDefined und Min-/MaxElements sollen wie auch bei Min-/Maximum als Ranges dargestellt werden
	
	private List<SettingsAttribute> attributesSettings = Collections.emptyList();
	
	private static String[] columnNames = new String[] {
			ConfigurationTerms.ATTRIBUTES,
			ConfigurationTerms.ATTRIBUTES_MIN,
			ConfigurationTerms.ATTRIBUTES_MAX,
			ConfigurationTerms.ATTRIBUTES_MINSIZE,
			ConfigurationTerms.ATTRIBUTES_MAXSIZE,
			ConfigurationTerms.ATTRIBUTES_VALUES 
	};
	
	public TableModelAttribute(List<SettingsAttribute> settings) {
		super();
		this.attributesSettings = settings;
	}

	@Override
	public int getRowCount() {
		if (attributesSettings != null) {
			return this.attributesSettings.size(); 
		} else {
			return 0;
		}
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		SettingsAttribute set = attributesSettings.get(row); 
		if ((set.getClassSettings().isAssociationClass()) && (column == 3 || column == 4)) {
			return false;
		} else if (set.isInherited()) {
			return false;
		} else if (column > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		SettingsAttribute set = attributesSettings.get(row);
		
		switch(col) {
		case 0: 
			return set.getAttribute().name();
		case 1:
			return set.getBounds().getLower();
		case 2:
			return set.getBounds().getUpper();
		case 3:
			return set.getCollectionSize().getLower();
		case 4:
			return set.getCollectionSize().getUpper();
		case 5:
			return StringUtil.fmtSeq(set.getValues(), ",");
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		SettingsAttribute set = this.attributesSettings.get(row);
		
		switch (column) {
		case 1:
			set.getBounds().setLower(aValue);
			break;
		case 2:
			set.getBounds().setUpper(aValue);
			break;
		case 3:
			set.getCollectionSize().setLower(aValue);
			break;
		case 4:
			set.getCollectionSize().setUpper(aValue);
			break;
		case 5:
			set.setValues((String) aValue);
			break;
		default:
			break;
		}
	}

	public void setClass(SettingsClass classSettings) {
		this.attributesSettings = new ArrayList<>(classSettings.getAttributeSettings().values());
		this.fireTableDataChanged();
	}

	public List<SettingsAttribute> getAttributesSettings() {
		return attributesSettings;
	}
	
}
