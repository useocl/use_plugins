package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class EditorInteger extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	JSpinner spinner;
	
	public EditorInteger() {
		super(new JTextField());
		spinner = new JSpinner(new SpinnerNumberModel(1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		spinner.setValue(value);
    	return spinner;
	}
	
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}

}
