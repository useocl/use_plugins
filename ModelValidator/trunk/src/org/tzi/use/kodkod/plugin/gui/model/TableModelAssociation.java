package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;
import org.tzi.use.kodkod.plugin.gui.LegendEntry;
import org.tzi.use.kodkod.plugin.gui.model.data.AssociationSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser;
import org.tzi.use.kodkod.plugin.gui.util.TextInputParser.Result;
import org.tzi.use.kodkod.plugin.gui.view.InputCheckingCell.Values;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class TableModelAssociation extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final List<AssociationSettings> allAssociationsSettings;
	private List<AssociationSettings> currentAssociationsSettings = Collections.emptyList();
	private List<Values<String>> editorValues = Collections.emptyList();

	private static final String[] COLUMN_NAMES = new String[] {
		ConfigurationTerms.ASSOCIATIONS,
		ConfigurationTerms.ASSOCIATIONS_MIN,
		ConfigurationTerms.ASSOCIATIONS_MAX,
		ConfigurationTerms.ASSOCIATIONS_VALUES
	};

	private static final String[] COLUMN_TOOLTIPS = new String[] {
		null,
		LegendEntry.ASSOCIATIONS_MINLINKS,
		LegendEntry.ASSOCIATIONS_MAXLINKS,
		LegendEntry.ASSOCIATIONS_PRESENTLINKS
	};
	
	public TableModelAssociation(List<AssociationSettings> settings) {
		allAssociationsSettings = settings;
		// initializing of editorValues happens in #setClass(ClassSettings)
	}

	@Override
	public int getRowCount() {
		return currentAssociationsSettings.size();
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
		return column > 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AssociationSettings set = currentAssociationsSettings.get(row);

		switch(col) {
		case 0:
			return set.getAssociation();
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
		AssociationSettings set = currentAssociationsSettings.get(row);

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
				Result<String> values = new TextInputParser(arg).parseAssociationValues(set.getAssociation());
				
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

	public void setClass(ClassSettings classSettings) {
		final IClass selectedClass = classSettings.getCls();
		List<AssociationSettings> associations = new ArrayList<AssociationSettings>(Collections2.filter(allAssociationsSettings, new Predicate<AssociationSettings>() {
			@Override
			public boolean apply(AssociationSettings input) {
				for (IAssociationEnd aEnd : input.getAssociation().associationEnds()) {
					// respects generalization
					if(aEnd.associatedClass().equals(selectedClass) || selectedClass.allParents().contains(aEnd.associatedClass())){
						return true;
					}
				}
				return false;
			}
		}));
		
		Collections.sort(associations, new Comparator<AssociationSettings>() {
			@Override
			public int compare(AssociationSettings o1, AssociationSettings o2) {
				IClass o1Class = o1.getAssociation().associationEnds().get(0).associatedClass();
				IClass o2Class = o2.getAssociation().associationEnds().get(0).associatedClass();
				
				boolean o1HasClassFirst = o1Class.equals(selectedClass);
				boolean o2HasClassFirst = o2Class.equals(selectedClass);
				Collection<IClass> parents = selectedClass.allParents();
				boolean o1ParentsHaveClassFirst = parents.contains(o1Class);
				boolean o2ParentsHaveClassFirst = parents.contains(o2Class);
				
				if(o1HasClassFirst){
					return o2HasClassFirst ? 0 : -1;
				} else if(o1ParentsHaveClassFirst){
					if(o2HasClassFirst){
						return 1;
					} else {
						return o2ParentsHaveClassFirst ? 0 : -1;
					}
//				} else if(o2HasClassFirst){
//					return 1;
				} else {
					return 1;
				}
			}
		});
		
		currentAssociationsSettings = associations;
		editorValues = new ArrayList<Values<String>>(associations.size());
		for(AssociationSettings as : associations){
			Values<String> v = new Values<String>();
			v.values = as.getInstanceNames();
			editorValues.add(v);
		}
		fireTableDataChanged();
	}

	public List<AssociationSettings> getAssociationsSettings() {
		return currentAssociationsSettings;
	}
	
	public void resetSavedValues() {
		for(int i = 0; i < currentAssociationsSettings.size(); i++){
			AssociationSettings settings = currentAssociationsSettings.get(i);
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
