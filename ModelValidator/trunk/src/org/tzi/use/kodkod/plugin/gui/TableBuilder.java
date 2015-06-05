package org.tzi.use.kodkod.plugin.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.TableModelClass;
import org.tzi.use.kodkod.plugin.gui.model.TableModelInteger;
import org.tzi.use.kodkod.plugin.gui.model.TableModelInvariant;
import org.tzi.use.kodkod.plugin.gui.model.TableModelOption;
import org.tzi.use.kodkod.plugin.gui.model.TableModelReal;
import org.tzi.use.kodkod.plugin.gui.model.TableModelString;
import org.tzi.use.kodkod.plugin.gui.model.TooltipTableModel;
import org.tzi.use.kodkod.plugin.gui.model.data.AssociationSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.AttributeSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.view.AssociationNameRenderer;
import org.tzi.use.kodkod.plugin.gui.view.AttributeNameRenderer;
import org.tzi.use.kodkod.plugin.gui.view.BoundsSpinner.BoundsSpinnerEditor;
import org.tzi.use.kodkod.plugin.gui.view.BoundsSpinner.BoundsSpinnerRenderer;
import org.tzi.use.kodkod.plugin.gui.view.ClassNameRenderer;
import org.tzi.use.kodkod.plugin.gui.view.RendererNonEditable;
import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinner.TableCellSpinnerEditor;
import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinner.TableCellSpinnerRenderer;
import org.tzi.use.kodkod.plugin.gui.view.TableCellSpinnerEditorReal;

public class TableBuilder {

	private static final int TABLE_ROW_HEIGHT = 20;
	private SettingsConfiguration allSettings;

	public TableBuilder(final SettingsConfiguration allSettings){
		this.allSettings = allSettings;
	}

	private JTable createBaseTable(TableModel model) {
		final JTable table = new JTable(model);

		table.setRowHeight(TABLE_ROW_HEIGHT);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// if table loses focus, editing will be stopped
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int realIndex = columnModel.getColumn(index).getModelIndex();
				
				if(table.getModel() instanceof TooltipTableModel){
					return ((TooltipTableModel) table.getModel()).getColumnTooltip(realIndex);
				} else {
					return null;
				}
			}
		};
		tableHeader.setReorderingAllowed(false);
		table.setTableHeader(tableHeader);
		return table;
	}

	private JTable basicTable(TableModel tm){
		JTable table = createBaseTable(tm);
		table.setPreferredScrollableViewportSize(new Dimension(0, table.getRowCount() * table.getRowHeight()));
		return table;
	}

	public JTable integer(){
		JTable table = basicTable(new TableModelInteger(allSettings.getIntegerTypeSettings()));
		table.setName(TypeConstants.INTEGER);
		
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new TableCellSpinnerRenderer(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		cm.getColumn(1).setCellRenderer(new TableCellSpinnerRenderer(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		cm.getColumn(0).setCellEditor(new TableCellSpinnerEditor(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		cm.getColumn(1).setCellEditor(new TableCellSpinnerEditor(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		//TODO Proof of Concept error handling
//		cm.getColumn(2).setCellEditor(new InputCheckingEditor());

		return table;
	}

	public JTable real(){
		JTable table = basicTable(new TableModelReal(allSettings.getRealTypeSettings()));
		table.setName(TypeConstants.REAL);
		
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new TableCellSpinnerRenderer(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.5d));
		cm.getColumn(1).setCellRenderer(new TableCellSpinnerRenderer(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.5d));
		
		cm.getColumn(0).setCellEditor(new TableCellSpinnerEditorReal(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.5d));
		cm.getColumn(1).setCellEditor(new TableCellSpinnerEditorReal(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.5d));

		return table;
	}

	public JTable string(){
		JTable table = basicTable(new TableModelString(allSettings.getStringTypeSettings()));
		table.setName(TypeConstants.STRING);
		
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new TableCellSpinnerRenderer(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		cm.getColumn(1).setCellRenderer(new TableCellSpinnerRenderer(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		cm.getColumn(0).setCellEditor(new TableCellSpinnerEditor(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
		cm.getColumn(1).setCellEditor(new TableCellSpinnerEditor(0, Integer.MIN_VALUE, Integer.MAX_VALUE));

		return table;
	}

	public JTable classes(){
		JTable table = createBaseTable(new TableModelClass(allSettings.getAllClassesSettings()));
		table.setName(ConfigurationTerms.CLASSES);

		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new ClassNameRenderer());
		cm.getColumn(1).setCellRenderer(new BoundsSpinnerRenderer(0));
		cm.getColumn(2).setCellRenderer(new BoundsSpinnerRenderer(0));
		cm.getColumn(3).setCellRenderer(new RendererNonEditable());
		
		cm.getColumn(1).setCellEditor(new BoundsSpinnerEditor(0));
		cm.getColumn(2).setCellEditor(new BoundsSpinnerEditor(0));
		
		return table;
	}

	public JTable attributes(){
		List<AttributeSettings> attributes =
				new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAttributeSettings().values());
		JTable table = createBaseTable(new TableModelAttribute(attributes));
		table.setName(ConfigurationTerms.ATTRIBUTES);
		DefaultTableCellRenderer rightAlignment = new DefaultTableCellRenderer();
		rightAlignment.setHorizontalAlignment( SwingConstants.RIGHT );
		table.getColumnModel().getColumn(5).setCellRenderer(rightAlignment);
		
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new AttributeNameRenderer());
		cm.getColumn(1).setCellRenderer(new BoundsSpinnerRenderer(-1));
		cm.getColumn(2).setCellRenderer(new BoundsSpinnerRenderer(-1));
		cm.getColumn(3).setCellRenderer(new BoundsSpinnerRenderer(0));
		cm.getColumn(4).setCellRenderer(new BoundsSpinnerRenderer(-1));
		cm.getColumn(5).setCellRenderer(new RendererNonEditable());
		
		cm.getColumn(1).setCellEditor(new BoundsSpinnerEditor(-1));
		cm.getColumn(2).setCellEditor(new BoundsSpinnerEditor(-1));
		cm.getColumn(3).setCellEditor(new BoundsSpinnerEditor(0));
		cm.getColumn(4).setCellEditor(new BoundsSpinnerEditor(-1));

		return table;
	}

	public JTable associations(){
		List<AssociationSettings> associations =
				new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAssociationSettings().values());
		JTable table = createBaseTable(new TableModelAssociation(associations));
		table.setName(ConfigurationTerms.ASSOCIATIONS);

		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setCellRenderer(new AssociationNameRenderer());
		cm.getColumn(1).setCellRenderer(new BoundsSpinnerRenderer(0));
		cm.getColumn(2).setCellRenderer(new BoundsSpinnerRenderer(-1));
		
		cm.getColumn(1).setCellEditor(new BoundsSpinnerEditor(0));
		cm.getColumn(2).setCellEditor(new BoundsSpinnerEditor(-1));
		
		return table;
	}

	public JTable options(){
		JTable table = createBaseTable(new TableModelOption(allSettings.getOptionSettings()));
		table.setPreferredScrollableViewportSize(new Dimension(350,table.getRowHeight()*table.getRowCount()));
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.setName(ConfigurationTerms.OPTIONS);
		return table;
	}

	public JTable invariants(){
		JTable table = createBaseTable(new TableModelInvariant(allSettings.getAllInvariantsSettings()));
		table.setPreferredScrollableViewportSize(new Dimension(800,table.getRowHeight()*table.getRowCount()));
		table.getColumnModel().getColumn(0).setPreferredWidth(400);
		table.setName(ConfigurationTerms.INVARIANTS);
		return table;
	}

}
