package org.tzi.use.kodkod.plugin.gui.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.IntegerSettings;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser.Result;
import org.tzi.use.kodkod.plugin.gui.view.InputCheckingCell.Values;

public class TableModelInteger extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final IntegerSettings settings;
	private final Values<Integer> editorValues;

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
		editorValues = new Values<Integer>();
		editorValues.values = settings.getValues();
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
			return editorValues; // StringUtil.fmtSeq(settings.getValues(), ",");
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setMinimum((Integer) aValue);
			break;
		case 1:
			settings.setMaximum((Integer) aValue);
			break;
		case 2:
			String arg = ((String) aValue).trim();
			
			Set<Integer> res = new LinkedHashSet<Integer>();
			if(!arg.isEmpty()){
				Result<Integer> values = new TextInputParser(arg).parseIntegerValues();
				
				res.addAll(values.getParsedValues());
				if(!values.getErrorValues().isEmpty()){
					editorValues.text = arg;
					editorValues.errors = values.getErrorValues();
				} else {
					editorValues.text = null;
					editorValues.errors = Collections.emptySet();
				}
			}
			editorValues.values = res;
			settings.setValues(res);
			break;
		}
		fireTableCellUpdated(row, column);
	}

	public IntegerSettings getSettings() {
		return settings;
	}

	public void resetSavedValues() {
		editorValues.reset();
		editorValues.values = settings.getValues();
	}

	public boolean inputsContainErrors(){
		return editorValues.text != null;
	}
	
}
