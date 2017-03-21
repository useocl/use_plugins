package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IClass;

public class ClassNameRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		setDescription((IClass) value);
		
		return c;
	}
	
	private void setDescription(IClass cls) {
		String classDescription = cls.name();
		if(cls instanceof IAssociationClass){
			classDescription += " (ac)";
		}
		
		if(cls.isAbstract()){
			setText("<html><i>" + classDescription + "</i></html>");
		} else {
			setText(classDescription);
		}
	}
	
}
