package org.tzi.use.kodkod.plugin.gui.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.IntegerSettings;
import org.tzi.use.util.StringUtil;

public class TableModelInteger extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final IntegerSettings settings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.INTEGER_MIN,
		ConfigurationTerms.INTEGER_MAX,
		ConfigurationTerms.INTEGER_VALUES
	};

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		LegendEntry.INT_MINIMUM,
		LegendEntry.INT_MAXIMUM,
		LegendEntry.INT_VALUES
	};
	
	public TableModelInteger(IntegerSettings set){
		settings = set;
	}

	@Override
	public int getRowCount() {
		return 1;
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
		return true;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
		case 0:
			return settings.getMinimum();
		case 1:
			return settings.getMaximum();
		case 2:
			return StringUtil.fmtSeq(settings.getValues(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setMinimum((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 1:
			settings.setMaximum((Integer) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			String arg = ((String) aValue).trim();
			Set<Integer> list = new LinkedHashSet<Integer>();
			if(!arg.isEmpty()){
				String[] split = arg.split(",");
				for (int i = 0; i < split.length; i++) {
					//TODO error handling
					list.add(Integer.valueOf(split[i].trim()));
				}
			}
			settings.setValues(list);
			fireTableCellUpdated(row, column);
			break;
		}
	}

	public IntegerSettings getSettings() {
		return settings;
	}

}
