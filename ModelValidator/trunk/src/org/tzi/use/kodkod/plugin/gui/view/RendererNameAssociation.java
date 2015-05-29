package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;

public class RendererNameAssociation extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererNameAssociation() {
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

		String string = "";
		TableModelAssociation assocModel = (TableModelAssociation) table.getModel();
		IAssociation assoc = assocModel.getAssociationsSettings().get(row).getAssociation();
		Iterator<IAssociationEnd> aes = assoc.associationEnds().iterator();
		string += assoc.name();
		if (aes.hasNext()) {
			string += " (";
		}
		while (aes.hasNext()) {
			IAssociationEnd ae = aes.next();
			string += ae.name()+":"+ae.associatedClass().name();
			if (aes.hasNext()) {
				string += ", ";
			} else {
				string += ")";
			}
		}

		setText(string);

		return this;
	}
}