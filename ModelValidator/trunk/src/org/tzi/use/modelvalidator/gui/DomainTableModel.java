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

public class DomainTableModel extends AbstractTableModel implements
		StateChangeListener {
	private static final long serialVersionUID = 1L;

	private List<Row> rows = new ArrayList<Row>();
	private List<String> columnTitles;

	public DomainTableModel(MSystem system) {
		system.addChangeListener(this);

		columnTitles = new ArrayList<String>();
		columnTitles.add("domain");
		columnTitles.add("values");
				
		for(MClass cls : system.model().classes()) {
			for(MAttribute attr : cls.allAttributes()){ 
				rows.add(new Row());
			}
		}
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
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Row row = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return row.getDomain();
		case 1:
			return row.getValues();
		default:
			return "Not handled!";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			rows.get(rowIndex).setDomain((String) aValue);
			return;
		case 1:
			rows.get(rowIndex).setValues((String) aValue);
			return;
		default:
			return;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public static class Row {

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getValues() {
			return values;
		}

		public void setValues(String values) {
			this.values = values;
		}

		private String domain = null;;
		private String values = null;
		
		public Row() {}

		
	}

	@Override
	public void stateChanged(StateChangeEvent e) {}
}
