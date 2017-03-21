package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser.Result;
import org.tzi.use.kodkod.plugin.gui.view.InputCheckingCell.Values;

public class TableModelClass extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final List<ClassSettings> classesSettings;
	private final List<Values<String>> editorValues;

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.CLASSES,
		ConfigurationTerms.CLASSES_MIN,
		ConfigurationTerms.CLASSES_MAX,
		ConfigurationTerms.CLASSES_VALUES };

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		null,
		LegendEntry.CLASS_MININSTANCES,
		LegendEntry.CLASS_MAXINSTANCES,
		LegendEntry.CLASS_INSTANCENAMES
	};
	
	public TableModelClass(List<ClassSettings> classesSettings) {
		this.classesSettings = classesSettings;
		editorValues = new ArrayList<Values<String>>(classesSettings.size());
		for(ClassSettings cs : classesSettings){
			Values<String> v = new Values<String>();
			v.values = cs.getInstanceNames();
			editorValues.add(v);
		}
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
	public String getColumnTooltip(int index) {
		return COLUMN_TOOLTIPS[index];
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
			return set.getCls();
		case 1:
			return set.getLowerBound();
		case 2:
			return set.getUpperBound();
		case 3:
			return editorValues.get(row);
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		ClassSettings set = classesSettings.get(row);

		switch (column) {
		case 1:
			set.setLowerBound((Integer) aValue);
			break;
		case 2:
			set.setUpperBound((Integer) aValue);
			break;
		case 3:
			String arg = ((String) aValue).trim();
			Values<String> currentValues = editorValues.get(row);
			
			Set<String> res = new LinkedHashSet<String>();
			if(!arg.isEmpty()){
				Result<String> values = new TextInputParser(arg).parseClassValues();
				
				res.addAll(values.getParsedValues());
				if(!values.getErrorValues().isEmpty()){
					currentValues.text = arg;
					currentValues.errors = values.getErrorValues();
				} else {
					currentValues.text = null;
					currentValues.errors = Collections.emptySet();
				}
			}
			currentValues.values = res;
			set.setInstanceNames(res);
			break;
		}
		fireTableCellUpdated(row, column);
	}

	public List<ClassSettings> getClassesSettings() {
		return classesSettings;
	}

	public void resetSavedValues() {
		for(int i = 0; i < classesSettings.size(); i++){
			ClassSettings settings = classesSettings.get(i);
			Values<String> values = editorValues.get(i);
			values.reset();
			values.values = settings.getInstanceNames();
		}		
	}

	public boolean inputsContainErrors(){
		for (Values<String> values : editorValues) {
			if(values.text != null){
				return true;
			}
		}
		return false;
	}
	
}
