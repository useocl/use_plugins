package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.util.Collections;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.util.StringUtil;

public class InputCheckingCell {
	
	public static final class Values<T> {
		public String text = null;
		public Set<T> values = Collections.emptySet();
		public Set<String> errors = Collections.emptySet();
		
		public void reset(){
			text = null;
			values = Collections.emptySet();
			errors = Collections.emptySet();
		}
	}

	private InputCheckingCell(){
	}
	
	public static class InputCheckingCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		
		private final Border defaultBorder;
		
		public InputCheckingCellRenderer() {
			super();
			defaultBorder = getBorder();
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(!table.isCellEditable(row, column)){
				return nonEditablePanel(hasFocus);
			}
			
			JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Values<?> r = (Values<?>) value;
			
			c.setText(formatContent(r));
			if(r.text != null){
				c.setBorder(BorderFactory.createLineBorder(Color.RED));
				c.setToolTipText(formatErrorTooltip(r));
			} else {
				if(hasFocus){
					c.setBorder(getFocusBorder(isSelected));
				} else {
					c.setBorder(defaultBorder);
				}
				c.setToolTipText(null);
			}
			
			return c;
		}
		
		private Component nonEditablePanel(boolean hasFocus) {
			JLabel p = new JLabel();
			p.setOpaque(true);
			p.setBackground(RendererNonEditable.NON_EDIT_BGCOLOR);
			if(hasFocus){
				p.setBorder(getFocusBorder(false));
			}
			return p;
		}
		
		private Border getFocusBorder(boolean isSelected) {
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
	
	public static class InputCheckingCellEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		
		private final Border defaultBorder;
		
		public InputCheckingCellEditor() {
			super(new JTextField());
			defaultBorder = ((JComponent) getComponent()).getBorder();
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JTextField c = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			Values<?> r = (Values<?>) value;
			
			c.setText(formatContent(r));
			if(r.text != null){
				c.setBorder(BorderFactory.createLineBorder(Color.RED));
				c.setToolTipText(formatErrorTooltip(r));
			} else {
				c.setBorder(defaultBorder);
				c.setToolTipText(null);
			}
			
			return c;
		}
	}
	
	private static String formatContent(Values<?> values) {
		if(values.text != null){
			return values.text;
		} else {
			return StringUtil.fmtSeq(values.values, ", ");
		}
	}
	
	private static String formatErrorTooltip(Values<?> values){
		return "<html>Some values could not be parsed. They will be omitted when saving:<br>" + StringUtil.fmtSeq(values.errors, ", ") + "</html>";
	}
	
}
