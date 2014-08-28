package org.tzi.use.kodkod.plugin.gui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ConfigurationTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ConfigurationTableModel(String[] columnNames, Object[][] data) {
		super();
		this.columnIdentifiers = new Vector<String>(Arrays.asList(columnNames));
		this.dataVector = new Vector<Vector<Object>>();
		for (int row = 0; row < data.length; row++) {
			Vector<Object> tempVector = new Vector<Object>();
			for (int col = 0; col < columnNames.length; col++) {
				tempVector.add(data[row][col]);
			}
			dataVector.addElement(tempVector);
		}
		fireTableDataChanged();
	}
	
	public boolean isCellEditable(int row, int col) {
        if (col < 1) {
            return false;
        } else {
            return true;
        }
	}

}
