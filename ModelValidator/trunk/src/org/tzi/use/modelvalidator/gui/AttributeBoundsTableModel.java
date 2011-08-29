package org.tzi.use.modelvalidator.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;

public class AttributeBoundsTableModel extends AbstractTableModel implements
		StateChangeListener {
	private static final long serialVersionUID = 1L;

	private List<Row> rows = new ArrayList<Row>();
	private List<String> columnTitles;

	public AttributeBoundsTableModel(MSystem system) {
		system.addChangeListener(this);

		ArrayList<MClass> classes = new ArrayList<MClass>(system.model()
				.classes());
		Collections.sort(classes, new Comparator<MClass>() {
			@Override
			public int compare(MClass o1, MClass o2) {
				return o1.name().compareTo(o2.name());
			}
		});

		for (MClass cls : classes) {
			ArrayList<MAttribute> attrs = new ArrayList<MAttribute>(
					cls.allAttributes());
			Collections.sort(attrs, new Comparator<MAttribute>() {
				@Override
				public int compare(MAttribute a1, MAttribute a2) {
					return a1.name().compareTo(a2.name());
				}
			});
			for (MAttribute attr : attrs) {
				rows.add(new Row(attr, system));
			}
		}

		columnTitles = new ArrayList<String>();
		columnTitles.add("class");
		columnTitles.add("attribute");
		columnTitles.add("concrete (mandatory)");
		columnTitles.add("undefined fix");
		columnTitles.add("set fix");
		columnTitles.add("domain");
		columnTitles.add("minimum number");
		columnTitles.add("maximum number");

	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	public List<Row> getRows() {
		return rows;
	}

	@Override
	public int getColumnCount() {
		return columnTitles.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnTitles.get(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Row row = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return row.getCls().name();
		case 1:
			return row.getAttr();
		case 2:
			return row.getConcreteValuesMandatoryFix();
		case 3:
			return row.getUndefinedFix();
		case 4:
			return row.getSetElementsFix();
		case 5:
			return row.getDomain();
		case 6:
			return row.getMinimumNumberOfDefinedValues();
		case 7:
			return row.getMaximumNumberOfDefinedValues();
		default:
			return "Not handled!";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 3:
			rows.get(rowIndex).setUndefinedFix((Boolean) aValue);
			return;
		case 4:
			rows.get(rowIndex).setSetElementsFix((Boolean) aValue);
			return;
		case 5:
			rows.get(rowIndex).setDomain((String) aValue);
			return;
		case 6:
			if (aValue == null)
				aValue = 0;
			rows.get(rowIndex).setMinimumNumberOfDefinedValues((Integer) aValue);
			return;
		case 7:
			if (aValue == null)
				aValue = 0;
			rows.get(rowIndex).setMaximumNumberOfDefinedValues((Integer) aValue);
			return;
		default:
			return;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 3:
			return Boolean.class;
		case 4:
			return Boolean.class;
		case 5:
			return String.class;
		default:
			return Integer.class;
		}
	}

	public static class Row {

		private MClass cls;
		private MAttribute attr;
		private String concreteValuesMandatoryFix = null;
		private int concreteDefinedValues = 0;
		private Boolean undefinedFix = false;
		private Boolean setElementsFix = null;
		private String domain = null;
		private int minimumNumberOfDefinedValues = 0;
		private int maximumNumberOfDefinedValues = 0;

		private MSystem system;

		public Row(MAttribute attr, MSystem system) {
			this.attr = attr;
			this.cls = attr.owner();
			this.system = system;
			init();
		}

		public void init() {
			concreteDefinedValues = 0;
			
			Set<MObject> objects = system.state().objectsOfClass(cls);

			ArrayList<MObject> objList = new ArrayList<MObject>(objects);
			Collections.sort(objList, new Comparator<MObject>() {
				@Override
				public int compare(MObject o1, MObject o2) {
					return o1.name().compareTo(o2.name());
				}
			});

			String attrMapping = "";
			for (MObject obj : objList) {
				Value attrValue = obj.state(system.state())
						.attributeValue(attr);
				if (attrMapping.equals("")) {
					attrMapping = obj.name() + "->"
							+ obj.state(system.state()).attributeValue(attr);
					if (attrValue.isDefined())
						concreteDefinedValues++;
				} else {
					attrMapping = attrMapping + ", " + obj.name() + "->"
							+ obj.state(system.state()).attributeValue(attr);
					if (attrValue.isDefined())
						concreteDefinedValues++;
				}
			}

			setConcreteValuesMandatoryFix(attrMapping);

			if (attr.type().isCollection(true)) {
				setSetElementsFix(false);
			}

			if (getMinimumNumberOfDefinedValues() < concreteDefinedValues)
				setMinimumNumberOfDefinedValues(concreteDefinedValues);
			if (minimumNumberOfDefinedValues > maximumNumberOfDefinedValues)
				setMaximumNumberOfDefinedValues(concreteDefinedValues);
		}

		public MClass getCls() {
			return cls;
		}

		public void setCls(MClass cls) {
			this.cls = cls;
		}

		public MAttribute getAttr() {
			return attr;
		}

		public void setCls(MAttribute attr) {
			this.attr = attr;
		}

		public String getConcreteValuesMandatoryFix() {
			return concreteValuesMandatoryFix;
		}

		public void setConcreteValuesMandatoryFix(
				String concreteValuesMandatoryFix) {
			if (concreteValuesMandatoryFix.equals("")) {
				this.concreteValuesMandatoryFix = null;
			} else {
				this.concreteValuesMandatoryFix = concreteValuesMandatoryFix;
			}
		}

		public Boolean getUndefinedFix() {
			return undefinedFix;
		}

		public void setUndefinedFix(Boolean undefinedFix) {
			this.undefinedFix = undefinedFix;
		}

		public Boolean getSetElementsFix() {
			return setElementsFix;
		}

		public void setSetElementsFix(Boolean setElementsFix) {
			this.setElementsFix = setElementsFix;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public int getMinimumNumberOfDefinedValues() {
			return minimumNumberOfDefinedValues;
		}

		public void setMinimumNumberOfDefinedValues(int minimumNumberOfDefinedValues) {
			this.minimumNumberOfDefinedValues = minimumNumberOfDefinedValues;
		}

		public int getMaximumNumberOfDefinedValues() {
			return maximumNumberOfDefinedValues;
		}

		public void setMaximumNumberOfDefinedValues(int maximumNumberOfDefinedValues) {
			this.maximumNumberOfDefinedValues = maximumNumberOfDefinedValues;
		}

		public void refresh() {
			init();
		}
	}

	@Override
	public void stateChanged(StateChangeEvent e) {
		Set<MAttribute> involvedAttributes = new HashSet<MAttribute>();
		for (MObject obj : e.getModifiedObjects()) {
			involvedAttributes.addAll(obj.cls().allAttributes());
		}
		for (MAttribute attr : involvedAttributes) {
			for (Row row : rows) {
				if (row.getAttr().equals(attr))
					row.refresh();
			}
		}
		this.fireTableChanged(new TableModelEvent(this));
	}
}
