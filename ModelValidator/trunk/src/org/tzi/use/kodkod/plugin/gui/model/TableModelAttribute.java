package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.AttributeSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.util.StringUtil;

public class TableModelAttribute extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private List<AttributeSettings> attributesSettings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.ATTRIBUTES,
		ConfigurationTerms.ATTRIBUTES_MIN,
		ConfigurationTerms.ATTRIBUTES_MAX,
		ConfigurationTerms.ATTRIBUTES_MINSIZE,
		ConfigurationTerms.ATTRIBUTES_MAXSIZE,
		ConfigurationTerms.ATTRIBUTES_VALUES
	};

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		null,
		LegendEntry.ATTRIBUTES_MINDEFINED,
		LegendEntry.ATTRIBUTES_MAXDEFINED,
		LegendEntry.ATTRIBUTES_MINELEMENTS,
		LegendEntry.ATTRIBUTES_MAXELEMENTS,
		LegendEntry.ATTRIBUTES_ATTRIBUTEVALUES
	};
	
	public TableModelAttribute(List<AttributeSettings> settings) {
		attributesSettings = settings;
	}

	@Override
	public int getRowCount() {
		return attributesSettings.size();
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
	public String getColumnTooltip(int index) {
		return COLUMN_TOOLTIPS[index];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		AttributeSettings set = attributesSettings.get(row);
		if (set.isInherited()) {
			return false;
		} else {
			return column > 0;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		AttributeSettings set = attributesSettings.get(row);

		switch(col) {
		case 0:
			return set.getAttribute().name();
		case 1:
			return set.getLowerBound();
		case 2:
			return set.getUpperBound();
		case 3:
			return set.getCollectionSizeMin();
		case 4:
			return set.getCollectionSizeMax();
		case 5:
			return StringUtil.fmtSeq(set.getInstanceNames(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		AttributeSettings set = attributesSettings.get(row);

		switch (column) {
		case 1:
			set.setLowerBound((Integer) aValue);
			break;
		case 2:
			set.setUpperBound((Integer) aValue);
			break;
		case 3:
			set.setCollectionSizeMin((Integer) aValue);
			break;
		case 4:
			set.setCollectionSizeMax((Integer) aValue);
			break;
		case 5:
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
		attributesSettings = new ArrayList<>(classSettings.getAttributeSettings().values());
		fireTableDataChanged();
	}

	public List<AttributeSettings> getAttributesSettings() {
		return attributesSettings;
	}

}
