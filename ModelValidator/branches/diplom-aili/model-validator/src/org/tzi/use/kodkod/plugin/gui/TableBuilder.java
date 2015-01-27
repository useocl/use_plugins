package org.tzi.use.kodkod.plugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAssociation;
import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.TableModelClass;
import org.tzi.use.kodkod.plugin.gui.model.TableModelInteger;
import org.tzi.use.kodkod.plugin.gui.model.TableModelInvariant;
import org.tzi.use.kodkod.plugin.gui.model.TableModelOption;
import org.tzi.use.kodkod.plugin.gui.model.TableModelReal;
import org.tzi.use.kodkod.plugin.gui.model.TableModelString;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAssociation;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsClass;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.view.RendererInteger;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAbstractAssociationClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAbstractClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAssociation;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAssociationClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameDerivedAttribute;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameInheritedAttribute;
import org.tzi.use.kodkod.plugin.gui.view.RendererNonEditable;
import org.tzi.use.kodkod.plugin.gui.view.RendererValues;

public class TableBuilder {
	//TODO: Alle Columnen mit ganzen Zahlen, sollen einen eigenen Renderer bekommen, der
	//1. rechtbuendig ist
	//2. bei einem Wert von -1 einen Stern(*), statt der -1 anzeigen soll
	//   wenn man -1 manuell eingibt, soll daraus bei Enter ein Stern vom Renderer wiedergegeben gegeben werden, aber in den Settings -1 eintragen
	//   wenn man ein Stern eingibt, soll da zwar auch ein Stern abgebildet werden, aber intern in den Settings als -1 gespeichert werden
	
	private SettingsConfiguration allSettings;
	
	public TableBuilder(final SettingsConfiguration allSettings){
		this.allSettings = allSettings;
	}
	
	private JTable createBaseTable(TableModel model) {
		JTable t = new JTable(model) {
			private static final long serialVersionUID = 1L;

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column == 0) {
					if (this.getName() != null) {
						String tableName = this.getName();
						switch (tableName) {
						case ConfigurationTerms.CLASSES: 
							TableModelClass classModel = (TableModelClass) this.getModel();
							SettingsClass clsSettings = classModel.getClassesSettings().get(row);
							if (clsSettings.getCls().isAbstract()) {
								if (clsSettings.isAssociationClass()) {
									return new RendererNameAbstractAssociationClass();
								}
								return new RendererNameAbstractClass();
							}
							if (clsSettings.isAssociationClass()) {
								return new RendererNameAssociationClass();
							}
							break;
						case ConfigurationTerms.ATTRIBUTES:
							TableModelAttribute attributeModel = (TableModelAttribute) this.getModel();
							SettingsAttribute attrSettings = attributeModel.getAttributesSettings().get(row);
							if (attrSettings.isInherited()) {
								return new RendererNameInheritedAttribute();
							}
							if (attrSettings.getAttribute().isDerived()) {
								return new RendererNameDerivedAttribute();
							}
							break;
						case ConfigurationTerms.ASSOCIATIONS:
							return new RendererNameAssociation();
						}
					}
					return super.getCellRenderer(row, column);
				} else if (!isCellEditable(row, column)) {
					return new RendererNonEditable();
				} else if ((column != getColumnCount()-1) && (getValueAt(row, column) instanceof Integer)){
					return new RendererInteger();
				} else if ((column == getColumnCount()-1) 
						&& this.getName() != ConfigurationTerms.OPTIONS 
						&& this.getName() != ConfigurationTerms.INVARIANTS) {
					return new RendererValues();
				} else {
					return super.getCellRenderer(row, column);
				}
			}
		};
		
		t.setRowHeight((int) (t.getRowHeight()*1.2));
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getTableHeader().setReorderingAllowed(false);
		return t;
	}
	
	private JTable createConfigurationTable(TableModel m){
		JTable t = createBaseTable(m);
		final JTableHeader tableHeader = new JTableHeader(t.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				String columnName = (String) columnModel.getColumn(index).getHeaderValue();
				return getToolTipBy(table.getName(), columnName);
			}
		};
		tableHeader.setReorderingAllowed(false);
		t.setTableHeader(tableHeader);
		return t;
	}
	
	public JTable integer(){
		JTable table = createConfigurationTable(new TableModelInteger(allSettings.getIntegerTypeSettings()));
		table.setName(TypeConstants.INTEGER);
		
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable real(){
		JTable table = createConfigurationTable(new TableModelReal(allSettings.getRealTypeSettings()));
		table.setName(TypeConstants.REAL);
		
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable string(){
		JTable table = createConfigurationTable(new TableModelString(allSettings.getStringTypeSettings()));
		table.setName(TypeConstants.STRING);
		
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable classes(){
		JTable table = createConfigurationTable(new TableModelClass(allSettings.getAllClassesSettings()));
		table.setName(ConfigurationTerms.CLASSES);
		
		return table;
	}
	
	public JTable attributes(){
		List<SettingsAttribute> attributes = 
					new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAttributeSettings().values());
		JTable table = createConfigurationTable(new TableModelAttribute(attributes));
		table.setName(ConfigurationTerms.ATTRIBUTES);
		DefaultTableCellRenderer rightAlignment = new DefaultTableCellRenderer();
		rightAlignment.setHorizontalAlignment( JLabel.RIGHT );
		table.getColumnModel().getColumn(5).setCellRenderer(rightAlignment);
		
		return table;
	}
	
	public JTable associations(){
		List<SettingsAssociation> associations = 
					new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAssociationSettings().values());
		JTable table = createConfigurationTable(new TableModelAssociation(associations));
		table.setName(ConfigurationTerms.ASSOCIATIONS);
		
		return table;
	}
	
	public JTable options(){
		JTable table = createBaseTable(new TableModelOption(allSettings.getOptionSettings()));
		table.getModel().setValueAt(PropertyEntry.aggregationcyclefreeness,0,0);
		table.getModel().setValueAt(PropertyEntry.forbiddensharing,1,0);
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
	
	private String getToolTipBy(String tableName, String columnName) {
		switch (tableName) {
			case (TypeConstants.INTEGER): {
				switch (columnName) {
				case (ConfigurationTerms.INTEGER_MIN): {
					return LegendEntry.INT_MINIMUM;
				}
				case (ConfigurationTerms.INTEGER_MAX): {
					return LegendEntry.INT_MAXIMUM;
				}
				case (ConfigurationTerms.INTEGER_VALUES): {
					return LegendEntry.INT_VALUES;
				}
				}
				break;
			}
			case (TypeConstants.REAL): {
				switch (columnName) {
				case (ConfigurationTerms.REAL_MIN): {
					return LegendEntry.REAL_MINIMUM;
				}
				case (ConfigurationTerms.REAL_MAX): {
					return LegendEntry.REAL_MAXIMUM;
				}
				case (ConfigurationTerms.REAL_STEP): {
					return LegendEntry.REAL_STEP;
				}
				case (ConfigurationTerms.REAL_VALUES): {
					return LegendEntry.REAL_VALUES;
				}
				}
				break;
			}
			case (TypeConstants.STRING): {
				switch (columnName) {
				case (ConfigurationTerms.STRING_MIN): {
					return LegendEntry.STRING_MINPRESENT;
				}
				case (ConfigurationTerms.STRING_MAX): {
					return LegendEntry.STRING_MAXPRESENT;
				}
				case (ConfigurationTerms.STRING_VALUES): {
					return LegendEntry.STRING_PRESENTSTRINGS;
				}
				}
				break;
			}
			case (ConfigurationTerms.CLASSES): {
				switch (columnName) {
				case (ConfigurationTerms.CLASSES_MIN): {
					return LegendEntry.CLASS_MININSTANCES;
				}
				case (ConfigurationTerms.CLASSES_MAX): {
					return LegendEntry.CLASS_MAXINSTANCES;
				}
				case (ConfigurationTerms.CLASSES_VALUES): {
					return LegendEntry.CLASS_INSTANCENAMES;
				}
				}
				break;
			}
			case (ConfigurationTerms.ATTRIBUTES): {
				switch (columnName) {
				case (ConfigurationTerms.ATTRIBUTES_MIN): {
					return LegendEntry.ATTRIBUTES_MINDEFINED;
				}
				case (ConfigurationTerms.ATTRIBUTES_MAX): {
					return LegendEntry.ATTRIBUTES_MAXDEFINED;
				}
				case (ConfigurationTerms.ATTRIBUTES_MINSIZE): {
					return LegendEntry.ATTRIBUTES_MINELEMENTS;
				}
				case (ConfigurationTerms.ATTRIBUTES_MAXSIZE): {
					return LegendEntry.ATTRIBUTES_MAXELEMENTS;
				}
				case (ConfigurationTerms.ATTRIBUTES_VALUES): {
					return LegendEntry.ATTRIBUTES_ATTRIBUTEVALUES;
				}
				}
				break;
			}
			case (ConfigurationTerms.ASSOCIATIONS): {
				switch (columnName) {
				case (ConfigurationTerms.ASSOCIATIONS_MIN): {
					return LegendEntry.ASSOCIATIONS_MINLINKS;
				}
				case (ConfigurationTerms.ASSOCIATIONS_MAX): {
					return LegendEntry.ASSOCIATIONS_MAXLINKS;
				}
				case (ConfigurationTerms.ASSOCIATIONS_VALUES): {
					return LegendEntry.ASSOCIATIONS_PRESENTLINKS;
				}
				}
				break;
			}
		}
		return null;
	}
	
	public static void repaintAllTables(Iterator<JTable> tables) {
		while (tables.hasNext()) {
			tables.next().repaint();
		}
	}
	
}
