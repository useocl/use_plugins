package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.impl.DerivedAttribute;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.AttributeSettings;

public class AttributeNameRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		row = table.getRowSorter().convertRowIndexToModel(row);
		AttributeSettings attributesSettings = ((TableModelAttribute) table.getModel()).getAttributesSettings().get(row);
		setDescription(attributesSettings.getAttribute(), attributesSettings.isInherited());
		
		return c;
	}
	
	private void setDescription(IAttribute attr, boolean inherited) {
		String attrDescription = attr.name();
		
		if(inherited){
			attrDescription += " (" + attr.owner().name() + ")";
		}
		if(attr instanceof DerivedAttribute){
			attrDescription += " (derived)";
		}
		setText(attrDescription);
	}
	
}
