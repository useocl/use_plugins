package org.tzi.use.modelvalidator.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;
import org.tzi.use.util.StringUtil;

public class ClassBoundsTableModel extends AbstractTableModel implements
		StateChangeListener {
	private static final long serialVersionUID = 1L;

	private List<Row> rows = new ArrayList<Row>();
	private List<String> columnTitles;

	public ClassBoundsTableModel(MSystem system) {
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
			rows.add(new Row(cls, system));
		}

		columnTitles = new ArrayList<String>();
		columnTitles.add("class");
		columnTitles.add("concrete mandatory (fix)");
		columnTitles.add("concrete mandatory (additional)");
		columnTitles.add("concrete optional");
		columnTitles.add("min number");
		columnTitles.add("max number");

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
		return columnIndex > 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Row row = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return row.getCls().name();
		case 1:
			return row.getConcreteObjectsMandatoryFix();
		case 2:
			return row.getConcreteObjectsMandatoryAdditional();
		case 3:
			return row.getConcreteObjectsOptional();
		case 4:
			return row.getMinimumNumberOfObjects();
		case 5:
			return row.getMaximumNumberOfObjects();
		default:
			return "Not handled!";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 2:
			rows.get(rowIndex).setConcreteObjectsMandatoryAdditional(
					(String) aValue);
			return;
		case 3:
			rows.get(rowIndex).setConcreteObjectsOptional((String) aValue);
			return;
		case 4:
			if (aValue == null)
				aValue = 0;
			rows.get(rowIndex).setMinimumNumberOfObjects((Integer) aValue);
			return;
		case 5:
			if (aValue == null)
				aValue = 0;
			rows.get(rowIndex).setMaximumNumberOfObjects((Integer) aValue);
			return;
		default:
			return;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 4:
			return Integer.class;
		case 5:
			return Integer.class;
		default:
			return String.class;
		}
	}

	public static class Row {

		private MClass cls;
		private String concreteObjectsMandatoryFix = null;
		private String concreteObjectsMandatoryAdditional = null;
		private String concreteObjectsOptional = null;
		private int minimumNumberOfObjects = 0;
		private int maximumNumberOfObjects = 0;

		private MSystem system;

		public Row(MClass cls, MSystem system) {
			this.cls = cls;
			this.system = system;
			init();
		}

		public void init() {
			Set<MObject> objects = system.state().objectsOfClass(cls);

			this.minimumNumberOfObjects = objects.size();
			if (minimumNumberOfObjects > maximumNumberOfObjects) {
				this.maximumNumberOfObjects = minimumNumberOfObjects;
			}

			if (!objects.isEmpty()) {
				setConcreteObjectsMandatoryFix(StringUtil.fmtSeq(objects, ", "));
			}
		}

		public MClass getCls() {
			return cls;
		}

		public void setCls(MClass cls) {
			this.cls = cls;
		}

		public String getConcreteObjectsMandatoryFix() {
			return concreteObjectsMandatoryFix;
		}

		public void setConcreteObjectsMandatoryFix(
				String concreteObjectsMandatoryFix) {
			this.concreteObjectsMandatoryFix = concreteObjectsMandatoryFix;
			if (getNumberOfMandatoryObjects() > minimumNumberOfObjects) {
				setMinimumNumberOfObjects(getNumberOfMandatoryObjects());
			}
		}

		public String getConcreteObjectsMandatoryAdditional() {
			return concreteObjectsMandatoryAdditional;
		}

		public void setConcreteObjectsMandatoryAdditional(
				String concreteObjectsMandatoryAdditional) {
			this.concreteObjectsMandatoryAdditional = concreteObjectsMandatoryAdditional;
			if (getNumberOfMandatoryObjects() > minimumNumberOfObjects) {
				setMinimumNumberOfObjects(getNumberOfMandatoryObjects());
			}
		}

		public String getConcreteObjectsOptional() {
			return concreteObjectsOptional;
		}

		public void setConcreteObjectsOptional(String concreteObjectsOptional) {
			this.concreteObjectsOptional = concreteObjectsOptional;
		}

		public int getMinimumNumberOfObjects() {
			return minimumNumberOfObjects;
		}

		public void setMinimumNumberOfObjects(int minimumNumberOfObjects) {
			if (minimumNumberOfObjects >= getNumberOfMandatoryObjects()) {
				this.minimumNumberOfObjects = minimumNumberOfObjects;
				if (minimumNumberOfObjects > maximumNumberOfObjects) {
					setMaximumNumberOfObjects(minimumNumberOfObjects);
				}
			}
		}

		public int getMaximumNumberOfObjects() {
			return maximumNumberOfObjects;
		}

		public void setMaximumNumberOfObjects(int maximumNumberOfObjects) {
			if (maximumNumberOfObjects >= minimumNumberOfObjects) {
				this.maximumNumberOfObjects = maximumNumberOfObjects;
			}
		}

		public void refresh() {
			init();
		}

		private int getNumberOfMandatoryObjects() {
			int sum = 0;
			if (concreteObjectsMandatoryFix != null) {
				sum += concreteObjectsMandatoryFix.split(",").length;
			}
			if (concreteObjectsMandatoryAdditional != null) {
				sum += concreteObjectsMandatoryAdditional.split(",").length;
			}
			return sum;
		}

	}

	@Override
	public void stateChanged(StateChangeEvent e) {
		List<MObject> newOrDeletedObjects = e.getDeletedObjects();
		newOrDeletedObjects.addAll(e.getNewObjects());
		Set<MClass> involvedClasses = new HashSet<MClass>();
		for (MObject obj : newOrDeletedObjects) {
			involvedClasses.add(obj.cls());
		}
		for (MClass cls : involvedClasses) {
			for (Row row : rows) {
				if (row.getCls().equals(cls))
					row.refresh();
			}
		}
		this.fireTableChanged(new TableModelEvent(this));
	}
}
