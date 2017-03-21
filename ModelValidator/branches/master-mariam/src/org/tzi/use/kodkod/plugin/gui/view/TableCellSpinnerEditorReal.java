package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.tzi.use.kodkod.plugin.gui.model.TableModelReal;
import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinner.TableCellSpinnerEditor;

public class TableCellSpinnerEditorReal extends TableCellSpinnerEditor {
	private static final long serialVersionUID = 1L;

	public TableCellSpinnerEditorReal(double value, double min, double max, double step) {
		super(value, min, max, step);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		TableModelReal tm = (TableModelReal) table.getModel();
		setStepSize(tm.getSettings().getStep());
		return c;
	}
	
	public void setStepSize(double step){
		((SpinnerNumberModel) model).setStepSize(step);
	}
}
