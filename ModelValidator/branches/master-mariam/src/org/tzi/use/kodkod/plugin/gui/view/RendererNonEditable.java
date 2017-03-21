package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererNonEditable extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public static final Color NON_EDIT_BGCOLOR = new Color(223, 223, 223);

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if(!table.isCellEditable(row, column)){
			return nonEditablePanel(hasFocus);
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
	
	private Component nonEditablePanel(boolean hasFocus) {
		JLabel p = new JLabel();
		p.setOpaque(true);
		p.setBackground(NON_EDIT_BGCOLOR);
		if(hasFocus){
			p.setBorder(getFocusBorder(false));
		}
		return p;
	}
	
	protected Border getFocusBorder(boolean isSelected) {
		Border border = null;
        if (isSelected) {
            border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        }
        if (border == null) {
            border = UIManager.getBorder("Table.focusCellHighlightBorder");
        }
        return border;
	}
	
}