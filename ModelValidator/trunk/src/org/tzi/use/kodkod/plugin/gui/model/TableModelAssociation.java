package org.tzi.use.kodkod.plugin.gui.model;

import java.util.ArrayList;
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
import org.tzi.use.util.StringUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class TableModelAssociation extends AbstractTableModel implements TooltipTableModel {
	private static final long serialVersionUID = 1L;

	private final List<AssociationSettings> allAssociationsSettings;
	private List<AssociationSettings> currentAssociationsSettings = Collections.emptyList();

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
			return StringUtil.fmtSeq(set.getInstanceNames(), ",");
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
			//TODO assoc tuple parsing
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
		final IClass selectedClass = classSettings.getCls();
		List<AssociationSettings> associations = new ArrayList<AssociationSettings>(Collections2.filter(allAssociationsSettings, new Predicate<AssociationSettings>() {
			@Override
			public boolean apply(AssociationSettings input) {
				for (IAssociationEnd aEnd : input.getAssociation().associationEnds()) {
					if(aEnd.associatedClass().equals(selectedClass)){
						return true;
					}
				}
				return false;
			}
		}));
		
		Collections.sort(associations, new Comparator<AssociationSettings>() {
			@Override
			public int compare(AssociationSettings o1, AssociationSettings o2) {
				boolean o1HasClassFirst = o1.getAssociation().associationEnds().get(0).associatedClass().equals(selectedClass);
				boolean o2HasClassFirst = o2.getAssociation().associationEnds().get(0).associatedClass().equals(selectedClass);
				if((o1HasClassFirst && o2HasClassFirst) || (!o1HasClassFirst && !o2HasClassFirst)){
					return o1.getAssociation().name().compareTo(o2.getAssociation().name());
				} else if(o1HasClassFirst) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		currentAssociationsSettings = associations;
		fireTableDataChanged();
	}

	public List<AssociationSettings> getAssociationsSettings() {
		return currentAssociationsSettings;
	}

}
