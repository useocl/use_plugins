package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererNameDerivedAttribute extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererNameDerivedAttribute() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (isSelected) {
			setBackground(new Color(204,204,255));
		} else {
			setBackground(table.getBackground());
		}
		
		setText(((String) table.getValueAt(row, column))+" (derived)");
		
		return this;
	}
}