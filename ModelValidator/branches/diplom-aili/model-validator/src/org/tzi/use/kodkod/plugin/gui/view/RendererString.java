package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.kodkod.plugin.gui.ConfigurationTerms;

public class RendererString extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererString() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (isSelected) {
			if (!table.getColumnModel().getColumn(0).getHeaderValue().equals(ConfigurationTerms.BASIC_TYPE)) {
				setBackground(new Color(204,204,255));
			} else {
				setBackground(Color.white);
			}
		} else {
			setBackground(table.getBackground());
		}
		
		this.setHorizontalAlignment(RIGHT);
		
		setText((String) table.getValueAt(row, column));
		
		return this;
	}
}
