package org.tzi.use.kodkod.plugin.gui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.tzi.use.kodkod.plugin.gui.view.NonEditableRenderer;

public class MVTable extends JTable {
	private static final long serialVersionUID = 1L;

	public MVTable(TableModel model) {
		super(model);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0) {
			return super.getCellRenderer(row, column);
		} else if (!isCellEditable(row, column)) {
			return new NonEditableRenderer();
		} else {
			return super.getCellRenderer(row, column);
		}
	}

}
