package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.tzi.use.kodkod.plugin.gui.model.TableModelReal;

public class EditorReal extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	JSpinner spinner;
	
	public EditorReal() {
		super(new JTextField());
		spinner = new JSpinner(new SpinnerNumberModel(0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.5));
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		TableModelReal model = (TableModelReal) table.getModel();
		((SpinnerNumberModel) spinner.getModel()).setStepSize(model.getSettings().getStep());
		spinner.setValue((Double) value );
    	return spinner;
	}
	
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}

}
