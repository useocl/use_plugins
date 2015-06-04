package org.tzi.use.kodkod.plugin.gui.view;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner;
import javax.swing.text.DefaultFormatterFactory;

import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinner.TableCellSpinnerEditor;
import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinner.TableCellSpinnerRenderer;

public class BoundsSpinner {
	
	private BoundsSpinner(){}
	
	public static class BoundsSpinnerRenderer extends TableCellSpinnerRenderer {
		private static final long serialVersionUID = 1L;
		
		public BoundsSpinnerRenderer(int minValue) {
			super(0, minValue, Integer.MAX_VALUE);
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor(); 
			JFormattedTextField tf = editor.getTextField();
			tf.setFormatterFactory(new DefaultFormatterFactory(new BoundsFormatter()));
		}
	}
	
	public static class BoundsSpinnerEditor extends TableCellSpinnerEditor {
		private static final long serialVersionUID = 1L;
		
		public BoundsSpinnerEditor(int minValue) {
			super(0, minValue, Integer.MAX_VALUE);
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor(); 
			JFormattedTextField tf = editor.getTextField();
			tf.setFormatterFactory(new DefaultFormatterFactory(new BoundsFormatter()));
		}
		
	}
	
	private static class BoundsFormatter extends AbstractFormatter {
		private static final long serialVersionUID = 1L;

		@Override
		public Object stringToValue(String text) throws ParseException {
			if(text.trim().equals("*")){
				return Integer.valueOf(-1);
			} else {
				return Integer.valueOf(text);
			}
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if(value.equals(Integer.valueOf(-1))){
				return "*";
			} else {
				return value.toString();
			}
		}
	}
	
}

