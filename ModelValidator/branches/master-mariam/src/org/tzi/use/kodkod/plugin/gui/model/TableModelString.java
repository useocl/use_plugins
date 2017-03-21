package org.tzi.use.kodkod.plugin.gui.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.StringSettings;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser.Result;
import org.tzi.use.kodkod.plugin.gui.view.InputCheckingCell.Values;

public class TableModelString extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final StringSettings settings;
	private final Values<String> editorValues;

	private static final String[] COLUMNS = new String[] {
		ConfigurationTerms.STRING_MIN,
		ConfigurationTerms.STRING_MAX,
		ConfigurationTerms.STRING_VALUES
	};

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		LegendEntry.STRING_MINPRESENT,
		LegendEntry.STRING_MAXPRESENT,
		LegendEntry.STRING_PRESENTSTRINGS,
	};
	
	public TableModelString(StringSettings settings) {
		this.settings = settings;
		editorValues = new Values<String>();
		editorValues.values = settings.getInstanceNames();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
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
			return settings.getLowerBound();
		case 1:
			return settings.getUpperBound();
		case 2:
			return editorValues;
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setLowerBound((Integer) aValue);
			break;
		case 1:
			settings.setUpperBound((Integer) aValue);
			break;
		case 2:
			String arg = ((String) aValue).trim();
			
			Set<String> res = new LinkedHashSet<String>();
			if(!arg.isEmpty()){
				Result<String> values = new TextInputParser(arg).parseStringValues();
				
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
			settings.setInstanceNames(res);
			break;
		}
		fireTableCellUpdated(row, column);
	}

	public StringSettings getSettings() {
		return settings;
	}

	public void resetSavedValues() {
		editorValues.reset();
		editorValues.values = settings.getInstanceNames();
	}
	
	public boolean inputsContainErrors(){
		return editorValues.text != null;
	}
	
}
