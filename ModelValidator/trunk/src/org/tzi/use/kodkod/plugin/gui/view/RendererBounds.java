package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererBounds extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererBounds() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground(new Color(204,204,255));
		} else {
			setBackground(table.getBackground());
		}

		setHorizontalAlignment(RIGHT);

		Object value = table.getValueAt(row, column);

		if (value.equals(Integer.valueOf(-1))) {
			setText("*");
		} else {
			setText(""+value);
		}

		return this;
	}
}
