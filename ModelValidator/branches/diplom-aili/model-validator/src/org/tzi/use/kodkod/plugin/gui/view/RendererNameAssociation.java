package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;

public class RendererNameAssociation extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public RendererNameAssociation() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (isSelected) {
			setBackground(new Color(204,204,255));
		} else {
			setBackground(table.getBackground());
		}
		
		String string = "";
		TableModelAssociation assocModel = (TableModelAssociation) table.getModel();
		MAssociation assoc = assocModel.getAssociationsSettings().get(row).getAssociation();
		Iterator<MAssociationEnd> aes = assoc.associationEnds().iterator();
		string += assoc.name();
		if (aes.hasNext()) {
			string += " (";
		}
		while (aes.hasNext()) {
			MAssociationEnd ae = aes.next();
			string += ae.name()+":"+ae.cls().name();
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