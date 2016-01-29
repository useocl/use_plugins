package org.tzi.use.kodkod.plugin.gui.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.RealSettings;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser.Result;
import org.tzi.use.kodkod.plugin.gui.view.InputCheckingCell.Values;

public class TableModelReal extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final RealSettings settings;
	private final Values<Double> editorValues;

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
		editorValues = new Values<Double>();
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
		switch (col) {
		case 0:
			return settings.getMinimum();
		case 1:
			return settings.getMaximum();
		case 2:
			return settings.getStep();
		case 3:
			return editorValues;
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 0:
			settings.setMinimum((Double) aValue);
			break;
		case 1:
			settings.setMaximum((Double) aValue);
			break;
		case 2:
			try {
				settings.setStep(Double.valueOf((String) aValue));
			}
			catch (NumberFormatException e) {
				// ignore new value if it is not a Double
			}
			break;
		case 3:
			String arg = ((String) aValue).trim();
			
			Set<Double> res = new LinkedHashSet<Double>();
			if(!arg.isEmpty()){
				Result<Double> values = new TextInputParser(arg).parseRealValues();
				
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

	public RealSettings getSettings() {
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
