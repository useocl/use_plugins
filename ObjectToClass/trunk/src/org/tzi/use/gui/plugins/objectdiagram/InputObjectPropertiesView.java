package org.tzi.use.gui.plugins.objectdiagram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.tzi.use.gui.plugins.OtcSystemApi;
import org.tzi.use.gui.plugins.Utilities;
import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.gui.plugins.data.TAttribute;
import org.tzi.use.gui.util.ExtendedJTable;
import org.tzi.use.gui.views.View;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.events.ObjectDestroyedEvent;
import org.tzi.use.uml.sys.events.tags.SystemStateChangedEvent;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class InputObjectPropertiesView extends JPanel implements View {
	private MSystem fSystem;

	private OtcSystemApi otcApi;

	private final MObject fObject;

	// TODO not really system
	/** the attributes as stored in the system */
	private List<MObject> fSystemAttributes;

	private final MAttribute IDENTITY_ATTRIBUTE;
	private final MAttribute CLASS_NAME_ATTRIBUTE;
	private final MAttribute ATTR_NAME_ATTRIBUTE;
	private final MAttribute ATTR_VALUE_ATTRIBUTE;

	private JTextField fIdentityTextField;
	private JTextField fClassNameTextField;
	private JTable fTable;
	private JScrollPane fTablePane;
	private JButton fBtnApply;
	private JButton fBtnReset;
	private JButton fBtnAddAttr;
	private JButton fBtnRemoveAttr;
	private TableModel fTableModel;

	private class TableModel extends AbstractTableModel {
		private final String[] columnNames = { "Attribute name", "Attribute value" };
		private List<TAttribute> tableData;

		private TableModel() {
			tableData = new ArrayList<TAttribute>();
		}

		private void initializeTableData() {
			tableData = new ArrayList<TAttribute>();
			for (MObject systemAttribute : fSystemAttributes) {
				tableData.add(Utilities.getTransformationAttribute(systemAttribute.state(fSystem.state())));
			}
			fireTableDataChanged();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return tableData.size();
		}

		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return tableData.get(row).getName();
			} else {
				return tableData.get(row).getValue();
			}
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {
			if (col == 0) {
				tableData.get(row).setName(value.toString());
			} else {
				tableData.get(row).setValue(value.toString());
			}
			fireTableCellUpdated(row, col);
		}
	}

	private void stopCellEditing() {
		if (fTable.getCellEditor() != null) {
			fTable.getCellEditor().stopCellEditing();
		}
	}

	public InputObjectPropertiesView(MSystem system, String objName, InputObjectDiagramView iodv) {
		super(new BorderLayout());
		fSystem = system;
		fSystem.registerRequiresAllDerivedValues();
		fSystem.getEventBus().register(this);

		otcApi = new OtcSystemApi(system);

		// initialize constant parts of the model
		MModel model = fSystem.model();
		IDENTITY_ATTRIBUTE = model.getClass(MMConstants.CLS_OBJECT_NAME).attribute(MMConstants.CLS_OBJECT_ATTR_IDENT,
				false);
		CLASS_NAME_ATTRIBUTE = model.getClass(MMConstants.CLS_OBJECT_NAME).attribute(MMConstants.CLS_OBJECT_ATTR_CLASSN,
				false);
		ATTR_NAME_ATTRIBUTE = model.getClass(MMConstants.CLS_SLOT_NAME).attribute(MMConstants.CLS_SLOT_ATTR_ATTR,
				false);
		ATTR_VALUE_ATTRIBUTE = model.getClass(MMConstants.CLS_SLOT_NAME).attribute(MMConstants.CLS_SLOT_ATTR_VAL,
				false);

		// create text fields
		fIdentityTextField = new JTextField();
		fIdentityTextField.getDocument().addDocumentListener(new DocumentChangeListener());
		fClassNameTextField = new JTextField();
		fClassNameTextField.getDocument().addDocumentListener(new DocumentChangeListener());

		// create table of attribute/value pairs
		fTableModel = new TableModel();
		fTableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				inputWasChanged();
			}
		});
		fTable = new ExtendedJTable(fTableModel);
		fTable.setPreferredScrollableViewportSize(new Dimension(250, 70));
		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTablePane = new JScrollPane(fTable);

		// create buttons
		fBtnApply = new JButton("Apply");
		fBtnApply.setMnemonic('A');
		fBtnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
				applyChanges();
			}
		});
		fBtnReset = new JButton("Reset");
		fBtnReset.setMnemonic('R');
		fBtnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
				initializeDataFromSystem();
			}
		});
		fBtnAddAttr = new JButton("Add attr.");
		fBtnAddAttr.setMnemonic('d');
		fBtnAddAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
				iodv.startSlotCreation(fObject);
			}
		});
		fBtnRemoveAttr = new JButton("Delete attr.");
		fBtnRemoveAttr.setMnemonic('e');
		fBtnRemoveAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MObject selectedAttr = getSelectedAttribute();
				if (selectedAttr != null) {
					stopCellEditing();
					iodv.startSlotDestruction(selectedAttr, fObject);
				}
			}
		});

		// JPanel identityPane = new JPanel();
		// identityPane.setLayout(new BoxLayout(identityPane,
		// BoxLayout.X_AXIS));
		// identityPane.add(new JLabel("Object name"));
		// identityPane.add(Box.createRigidArea(new Dimension(10, 0)));
		// identityPane.add(fIdentityTextField);
		//
		// JPanel classNamePane = new JPanel();
		// classNamePane.setLayout(new BoxLayout(classNamePane,
		// BoxLayout.X_AXIS));
		// classNamePane.add(new JLabel("Class name"));
		// classNamePane.add(Box.createRigidArea(new Dimension(10, 0)));
		// classNamePane.add(fClassNameTextField);

		// layout for the textField area
		JPanel textFieldPane = new JPanel();
		textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.Y_AXIS));
		textFieldPane.add(new JLabel("Object name"));
		textFieldPane.add(fIdentityTextField);
		textFieldPane.add(new JLabel("Class name"));
		textFieldPane.add(fClassNameTextField);
		// textFieldPane.add(identityPane);
		// textFieldPane.add(classNamePane);

		// layout the buttons centered from left to right
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		// buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(fBtnApply);
		// buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(fBtnReset);
		buttonPane.add(fBtnAddAttr);
		buttonPane.add(fBtnRemoveAttr);
		buttonPane.add(Box.createHorizontalGlue());

		// layout panel
		add(textFieldPane, BorderLayout.NORTH);
		add(fTablePane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);
		setSize(new Dimension(300, 300));

		// initialize with actual data
		MSystemState state = fSystem.state();
		fObject = state.objectByName(objName);
		initializeDataFromSystem();
	}

	private class DocumentChangeListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			inputWasChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			inputWasChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			inputWasChanged();
		}
	}

	private void inputWasChanged() {
		addAndRemoveButtonsSetEnabled(false);
	}

	private void addAndRemoveButtonsSetEnabled(boolean b) {
		// fBtnApply.setEnabled(true);
		fBtnAddAttr.setEnabled(b);
		fBtnRemoveAttr.setEnabled(b);
	}

	private MObject getSelectedAttribute() {
		if (fTable.getSelectedRow() == -1) {
			// nothing is selected
			return null;
		}
		return fSystemAttributes.get(fTable.getSelectedRow());
	}

	/**
	 * Applies changes by setting new attribute values. Entries may be arbitrary
	 * OCL expressions.
	 */
	private void applyChanges() {
		// Don't refresh after first change...
		fSystem.getEventBus().unregister(this);
		boolean error = false;

		// constant parts of later expressions
		MModel model = fSystem.model();
		MSystemState state = fSystem.state();
		String input = "<input>";
		StringWriter errorOutputWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(errorOutputWriter, true);
		VarBindings varBindings = fSystem.varBindings();
		String errorCompile = "Error compiling expression";
		String errorExecute = "Error executing expression";

		// textField part
		String identityText = fIdentityTextField.getText();
		if (!Utilities.equalsWithoutApostrophes(otcApi.getIdentityOfObject(fObject), identityText)) {
			error = applyStringAttributeChanges(fObject, IDENTITY_ATTRIBUTE, identityText, printWriter, error);
		}
		String classNameText = fClassNameTextField.getText();
		if (!Utilities.equalsWithoutApostrophes(otcApi.getClassNameOfObject(fObject), classNameText)) {
			error = applyStringAttributeChanges(fObject, CLASS_NAME_ATTRIBUTE, classNameText, printWriter, error);
		}

		for (int i = 0; !error && i < fSystemAttributes.size(); ++i) {
			TAttribute newValue = fTableModel.tableData.get(i);
			MObject systemAttribute = fSystemAttributes.get(i);

			// if (!newValue.equals(oldValue)) { TODO so oder anders
			error = applyStringAttributeChanges(systemAttribute, ATTR_NAME_ATTRIBUTE, newValue.getName(), printWriter,
					error);

			String newAttrValue = newValue.getValue();
			Expression attrValueAsExpression = null;
			if (newAttrValue != null) {
				attrValueAsExpression = OCLCompiler.compileExpression(model, state, newAttrValue, input, printWriter,
						varBindings);
			}
			if (attrValueAsExpression == null) {
				// if the value could not be compiled on the first try, try
				// again with a String value
				newAttrValue = Utilities.setApostrophes(newAttrValue);
				attrValueAsExpression = OCLCompiler.compileExpression(model, state, newAttrValue, input, printWriter,
						varBindings);
			}
			if (attrValueAsExpression == null) {
				JOptionPane.showMessageDialog(this, errorOutputWriter, errorCompile, JOptionPane.ERROR_MESSAGE);
				error = true;
			} else {
				try {
					fSystem.execute(new MAttributeAssignmentStatement(systemAttribute, ATTR_VALUE_ATTRIBUTE,
							attrValueAsExpression));
				} catch (MSystemException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), errorExecute, JOptionPane.ERROR_MESSAGE);
					error = true;
				}
			}
		}
		addAndRemoveButtonsSetEnabled(true);
		fSystem.getEventBus().register(this);
	}

	/**
	 * applies changes to an attribute of type String
	 * 
	 * @param obj
	 *            the object to change
	 * @param attr
	 *            the attribute to change
	 * @param text
	 *            the new text
	 * @param printWriter
	 *            output stream for error messages
	 * @param error
	 *            has an error happened before?
	 * @return has an error happened?
	 */
	private boolean applyStringAttributeChanges(MObject obj, MAttribute attr, String text, PrintWriter printWriter,
			boolean error) {
		String textWithApostrophes = Utilities.setApostrophes(text);
		Expression expression = OCLCompiler.compileExpression(fSystem.model(), fSystem.state(), textWithApostrophes,
				"<input>", printWriter, fSystem.varBindings());
		if (expression == null) {
			System.out.println("Error compiling expression");
			return true;
		}
		try {
			fSystem.execute(new MAttributeAssignmentStatement(obj, attr, expression));
		} catch (MSystemException e) {
			System.out.println("Error executing expression");
			return true;
		}
		return error;
	}

	private void initializeDataFromSystem() {
		// identity/className part
		fIdentityTextField.setText(Utilities.trim(otcApi.getIdentityOfObject(fObject)));
		fClassNameTextField.setText(Utilities.trim(otcApi.getClassNameOfObject(fObject)));

		// attribute part
		fSystemAttributes = otcApi.getSlotsOfObject(fObject, true);

		// now that the system attributes are saved, the table data can be
		// initialized
		fTableModel.initializeTableData();

		addAndRemoveButtonsSetEnabled(true);
	}

	// TODO better would be to close the window instead
	private boolean objectDestroyed = false;

	@Subscribe
	public void onStateChanged(SystemStateChangedEvent e) {
		if (e instanceof ObjectDestroyedEvent) {
			ObjectDestroyedEvent ode = (ObjectDestroyedEvent) e;
			if (ode.getDestroyedObject().equals(fObject)) {
				setVisible(false);
				objectDestroyed = true;
			}
		}
		if (!objectDestroyed) {
			initializeDataFromSystem();
		}
	}

	/**
	 * Detaches the view from its model.
	 */
	public void detachModel() {
		fSystem.getEventBus().unregister(this);
		fSystem.unregisterRequiresAllDerivedValues();
	}
}