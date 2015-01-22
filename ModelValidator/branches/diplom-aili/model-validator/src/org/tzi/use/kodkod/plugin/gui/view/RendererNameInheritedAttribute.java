package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;

public class RendererNameInheritedAttribute extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererNameInheritedAttribute() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (isSelected) {
			setBackground(new Color(204,204,255));
		} else {
			setBackground(table.getBackground());
		}
		
		TableModelAttribute attrModel = (TableModelAttribute) table.getModel();
		String inheritedClass = attrModel.getAttributesSettings().get(row).getAttribute().owner().name();
		
		setText(((String) table.getValueAt(row, column))+" ("+inheritedClass+")");
		
		return this;
	}
}