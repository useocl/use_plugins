package org.tzi.use.kodkod.plugin.gui.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.RealSettings;
import org.tzi.use.util.StringUtil;

public class TableModelReal extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final RealSettings settings;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.REAL_MIN,
		ConfigurationTerms.REAL_MAX,
		ConfigurationTerms.REAL_STEP,
		ConfigurationTerms.REAL_VALUES
	};

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		LegendEntry.REAL_MINIMUM,
		LegendEntry.REAL_MAXIMUM,
		LegendEntry.REAL_STEP,
		LegendEntry.REAL_VALUES
	};
	
	public TableModelReal(RealSettings settings) {
		this.settings = settings;
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
		switch (col) {
		case 0:
			return settings.getMinimum();
		case 1:
			return settings.getMaximum();
		case 2:
			return settings.getStep();
		case 3:
			return StringUtil.fmtSeq(settings.getValues(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setMinimum((Double) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 1:
			settings.setMaximum((Double) aValue);
			fireTableCellUpdated(row, column);
			break;
		case 2:
			settings.setStep(Double.valueOf((String) aValue));
			fireTableCellUpdated(row, column);
			break;
		case 3:
			String arg = ((String) aValue).trim();
			Set<Double> list = new LinkedHashSet<Double>();
			if(!arg.isEmpty()){
				String[] split = arg.split(",");
				for (int i = 0; i < split.length; i++) {
					//TODO error handling
					list.add(Double.valueOf(split[i].trim()));
				}
			}
			settings.setValues(list);
			fireTableCellUpdated(row, column);
			break;
		}
	}

	public RealSettings getSettings() {
		return settings;
	}

}
