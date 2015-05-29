package org.tzi.use.kodkod.plugin.gui.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class EditorBounds extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	JSpinner spinner;
	JSpinner.NumberEditor editor;
	JTextField textField;
	boolean valueSet;
	
	public EditorBounds(int minimum) {
		super(new JTextField());
		spinner = new JSpinner(new SpinnerNumberModel(1, minimum, Integer.MAX_VALUE, 1));
		editor = (NumberEditor) spinner.getEditor();
		textField = editor.getTextField();
		textField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fe) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (valueSet) {
							textField.setCaretPosition(1);
						}
					}
				});
			}

			public void focusLost(FocusEvent fe) {
			}
		});
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				stopCellEditing();
			}
		});
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (!valueSet) {
			spinner.setValue(value);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textField.requestFocus();
			}
		});
		
		return spinner;
	}
	
	@Override
	public boolean isCellEditable(EventObject eo) {
		if (eo instanceof KeyEvent) {
			KeyEvent ke = (KeyEvent) eo;
			textField.setText(String.valueOf(ke.getKeyChar()));
			valueSet = true;
		} else {
			valueSet = false;
		}
		return true;
	}
	
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}
	
	@Override
	public boolean stopCellEditing() {
		try {
			spinner.commitEdit();
		} catch (java.text.ParseException e) {
			JOptionPane.showMessageDialog(null,
					"Value must be integer and at least -1.");
		}
		return super.stopCellEditing();
	}

}
