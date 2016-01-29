package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.use.util.StringUtil;

public class AssociationNameRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		setDescription((IAssociation) value);

		return c;
	}
	
	private void setDescription(IAssociation assoc) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(assoc.name());
		sb.append(" (");
		StringUtil.fmtSeq(sb, assoc.associationEnds(), ", ", new StringUtil.IElementFormatter<IAssociationEnd>() {
			@Override
			public String format(IAssociationEnd element) {
				return element.name() + ":" + element.associatedClass().name();
			}
		});
		sb.append(")");
		
		setText(sb.toString());
	}
}