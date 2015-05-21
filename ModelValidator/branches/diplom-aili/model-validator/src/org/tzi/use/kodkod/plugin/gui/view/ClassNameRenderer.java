package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.kodkod.plugin.gui.model.TableModelClass;

@SuppressWarnings("serial")
public class ClassNameRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		IClass cls = ((TableModelClass) table.getModel()).getClassesSettings().get(row).getCls();
		setDescription(cls);
		
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
