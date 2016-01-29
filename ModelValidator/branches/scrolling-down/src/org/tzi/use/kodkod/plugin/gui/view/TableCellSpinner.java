package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import sun.swing.DefaultLookup;

public abstract class TableCellSpinner extends AbstractCellEditor {
	private static final long serialVersionUID = 1L;

	public static class TableCellSpinnerRenderer extends TableCellSpinner implements TableCellRenderer {
		private static final Color NON_EDIT_BGCOLOR = new Color(223, 223, 223);
		private static final long serialVersionUID = 1L;

		public TableCellSpinnerRenderer(int value, int min, int max) {
			super(value, min, max);
		}
		
		public TableCellSpinnerRenderer(double value, double min, double max, double step) {
			super(value, min, max, step);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(!table.isCellEditable(row, column)){
				return nonEditablePanel(hasFocus);
			}
			
			setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
			if(hasFocus){
				setBorder(getFocusBorder(isSelected));
			} else {
				setBorder(null);
			}
			
			if(value != null){
				spinner.setValue(value);
			}
			return spinner;
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
	}
	
	public static class TableCellSpinnerEditor extends TableCellSpinner implements TableCellEditor {
		private static final long serialVersionUID = 1L;

		public TableCellSpinnerEditor(int value, int min, int max) {
			super(value, min, max);
		}
		
		public TableCellSpinnerEditor(double value, double min, double max, double step) {
			super(value, min, max, step);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			setBorder(DefaultLookup.getBorder(spinner, spinner.getUI(), "Table.focusCellHighlightBorder"));
			
			if(value != null){
				spinner.setValue(value);
			}
			return spinner;
		}
	}
	
	protected JSpinner spinner;
	protected SpinnerModel model;
	
	public TableCellSpinner(int value, int min, int max) {
		model = new SpinnerNumberModel(value, min, max, 1);
		spinner = createSpinner(model);
	}
	
	public TableCellSpinner(double value, double min, double max, double step) {
		model = new SpinnerNumberModel(value, min, max, step);
		spinner = createSpinner(model);
		
		//FIXME makes font different
//		NumberEditor fractionEditor = new NumberEditor(spinner, "0,0");
//		spinner.setEditor(fractionEditor);
	}

	private JSpinner createSpinner(SpinnerModel model) {
		final JSpinner spinner = new JSpinner(model);
		// remove spinner border in favor of table grid
		spinner.setBorder(null);
		// add space between text field and spinner buttons
		spinner.getEditor().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.WHITE));
		return spinner;
	}
	
	public void setBackground(Color c) {
		Component component = spinner.getEditor().getComponent(0);
		component.setBackground(c);
		spinner.getEditor().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, c));
	}
	
	public void setBorder(Border b) {
		spinner.setBorder(b);
	}
	
	protected Border getFocusBorder(boolean isSelected) {
		Border border = null;
        if (isSelected) {
            border = DefaultLookup.getBorder(spinner, spinner.getUI(), "Table.focusSelectedCellHighlightBorder");
        }
        if (border == null) {
            border = DefaultLookup.getBorder(spinner, spinner.getUI(), "Table.focusCellHighlightBorder");
        }
        return border;
	}
	
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}

}
