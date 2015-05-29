package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererReal extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererReal() {
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

		Object value = table.getValueAt(row, column);
		if (value != null) {
			if (value.equals("")) {
				setText("");
			} else if (value instanceof String) {
				value = Double.parseDouble((String)value);
			} else {
				setText(""+value);
			}
		} else {
			setText("");
		}

		setHorizontalAlignment(RIGHT);

		return this;
	}
}
