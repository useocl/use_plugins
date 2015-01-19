package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class NonEditableRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	public NonEditableRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		setBackground(table.getParent().getBackground());
		
		return this;
	}
}