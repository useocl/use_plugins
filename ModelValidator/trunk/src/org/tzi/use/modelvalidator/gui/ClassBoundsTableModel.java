package org.tzi.use.modelvalidator.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.StringUtil;

public class ClassBoundsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<Row> rows = new ArrayList<Row>();
	private List<String> columnTitles;

	public ClassBoundsTableModel(MSystem system) {
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
		columnTitles.add("Class");
		columnTitles.add("Concrete objects (lower)");
		columnTitles.add("Concrete objects (upper)");
		columnTitles.add("Minimum number of objects");
		columnTitles.add("Maximum number of objects");

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
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Row row = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return row.getCls().name();
		case 1:
			return row.getConcreteObjectsLower();
		case 2:
			return row.getConcreteObjectsUpper();
		case 3:
			return row.getMinimumNumberOfObjects();
		case 4:
			return row.getMaximumNumberOfObjects();
		default:
			return "Not handled!";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return Integer.class;
		case 4:
			return Integer.class;
		default:
			return String.class;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 1:
			rows.get(rowIndex).setConcreteObjectsLower((String)aValue);
			return;
		case 2:
			rows.get(rowIndex).setConcreteObjectsUpper((String)aValue);
			return;
		case 3:
			rows.get(rowIndex).setMinimumNumberOfObjects((Integer)aValue);
			return;
		case 4:
			rows.get(rowIndex).setMaximumNumberOfObjects((Integer)aValue);
			return;
		default:
			return;
		}
	}

	private static class Row {
		private MClass cls;
		private String concreteObjectsLower = "";
		private String concreteObjectsUpper = "";
		private int minimumNumberOfObjects = 0;
		private int maximumNumberOfObjects = 0;

		public Row(MClass cls, MSystem system) {
			this.cls = cls;
			Set<MObject> objects = system.state().objectsOfClass(cls);

			this.minimumNumberOfObjects = objects.size();
			this.maximumNumberOfObjects = minimumNumberOfObjects;

			this.concreteObjectsLower = StringUtil.fmtSeq(objects, ", ");
			this.concreteObjectsUpper = concreteObjectsLower;
		}

		public MClass getCls() {
			return cls;
		}

		public void setCls(MClass cls) {
			this.cls = cls;
		}

		public String getConcreteObjectsLower() {
			return concreteObjectsLower;
		}

		public void setConcreteObjectsLower(String concreteObjectsLower) {
			System.out.println(concreteObjectsLower);
			this.concreteObjectsLower = concreteObjectsLower;
		}

		public String getConcreteObjectsUpper() {
			return concreteObjectsUpper;
		}

		public void setConcreteObjectsUpper(String concreteObjectsUpper) {
			System.out.println(concreteObjectsUpper);
			this.concreteObjectsUpper = concreteObjectsUpper;
		}

		public int getMinimumNumberOfObjects() {
			return minimumNumberOfObjects;
		}

		public void setMinimumNumberOfObjects(int minimumNumberOfObjects) {
			System.out.println(minimumNumberOfObjects);
			this.minimumNumberOfObjects = minimumNumberOfObjects;
		}

		public int getMaximumNumberOfObjects() {
			return maximumNumberOfObjects;
		}

		public void setMaximumNumberOfObjects(int maximumNumberOfObjects) {
			System.out.println(maximumNumberOfObjects);
			this.maximumNumberOfObjects = maximumNumberOfObjects;
		}

	}
}
