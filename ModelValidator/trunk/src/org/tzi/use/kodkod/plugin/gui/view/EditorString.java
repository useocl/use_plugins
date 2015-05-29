package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class EditorString extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	
	public EditorString() {
		super(new JTextField());
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JTextField editor = (JTextField) super.getTableCellEditorComponent(	table, value, isSelected, row, column);
		
		if (value != null){
			editor.setText(String.valueOf(value));
		}
		editor.setHorizontalAlignment(SwingConstants.RIGHT);
		
		return editor;
	}

}
