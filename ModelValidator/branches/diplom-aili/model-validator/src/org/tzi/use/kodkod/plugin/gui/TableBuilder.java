package org.tzi.use.kodkod.plugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import org.tzi.kodkod.model.config.impl.PropertyEntry;
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
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;

public class TableBuilder {
	
	private SettingsConfiguration allSettings;
	
	public TableBuilder(SettingsConfiguration allSettings){
		this.allSettings = allSettings;
	}
	
	public JTable integer(){
		JTable table = new JTable(new TableModelInteger(allSettings.getIntegerTypeSettings()));
		table.setName(ConfigurationConversion.INTEGER);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.getModel().setValueAt("<html><b>Integer</b></html>",0,0);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable real(){
		JTable table = new JTable(new TableModelReal(allSettings.getRealTypeSettings()));
		table.setName(ConfigurationConversion.REAL);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.getModel().setValueAt("<html><b>Real</b></html>",0,0);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable string(){
		JTable table = new JTable(new TableModelString(allSettings.getStringTypeSettings()));
		table.setName(ConfigurationConversion.STRING);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.getModel().setValueAt("<html><b>String</b></html>",0,0);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(),table.getRowHeight()*table.getRowCount()));
		table.setSelectionBackground(Color.white);
		return table;
	}
	
	public JTable classes(){
		JTable table = new JTable(new TableModelClass(allSettings.getAllClassesSettings()));
		table.setName(ConfigurationConversion.CLASSES);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	public JTable attributes(){
		//TODO: Bei spaeterem ein und ausblenden, muessen irgendwie die Tooltips an die wechselnden TableHeader angepasst werden
		List<SettingsAttribute> attributes = 
					new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAttributeSettings().values());
		JTable table = new JTable(new TableModelAttribute(attributes));
		table.setName(ConfigurationConversion.ATTRIBUTES);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	public JTable associations(){
		List<SettingsAssociation> associations = 
					new ArrayList<>(allSettings.getAllClassesSettings().get(0).getAssociationSettings().values());
		JTable table = new JTable(new TableModelAssociation(associations));
		table.setName(ConfigurationConversion.ASSOCIATIONS);
		final JTableHeader tableHeader = new JTableHeader(table.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        String columnName = (String) columnModel.getColumn(index).getHeaderValue();
                        return getToolTipBy(table.getName(), columnName);
            }
		};
		table.setTableHeader(tableHeader);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	public JTable options(){
		JTable table = new JTable(new TableModelOption(allSettings.getOptionSettings()));
		table.getModel().setValueAt(PropertyEntry.aggregationcyclefreeness,0,0);
		table.getModel().setValueAt(PropertyEntry.forbiddensharing,1,0);
		table.setPreferredScrollableViewportSize(new Dimension(350,table.getRowHeight()*table.getRowCount()));
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	public JTable invariants(){
		JTable table = new JTable(new TableModelInvariant(allSettings.getAllInvariantsSettings()));
		table.setPreferredScrollableViewportSize(new Dimension(800,table.getRowHeight()*table.getRowCount()));
		table.getColumnModel().getColumn(0).setPreferredWidth(400);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	private String getToolTipBy(String tableName, String columnName) {
		if (tableName == null || columnName == null) {
			return null;
		}
		switch (tableName) {
			case (ConfigurationConversion.INTEGER): {
				switch (columnName) {
				case (ConfigurationConversion.INTEGER_MIN): {
					return LegendEntry.INT_MINIMUM;
				}
				case (ConfigurationConversion.INTEGER_MAX): {
					return LegendEntry.INT_MAXIMUM;
				}
				case (ConfigurationConversion.INTEGER_VALUES): {
					return LegendEntry.INT_VALUES;
				}
				}
				break;
			}
			case (ConfigurationConversion.REAL): {
				switch (columnName) {
				case (ConfigurationConversion.REAL_MIN): {
					return LegendEntry.REAL_MINIMUM;
				}
				case (ConfigurationConversion.REAL_MAX): {
					return LegendEntry.REAL_MAXIMUM;
				}
				case (ConfigurationConversion.REAL_STEP): {
					return LegendEntry.REAL_STEP;
				}
				case (ConfigurationConversion.REAL_VALUES): {
					return LegendEntry.REAL_VALUES;
				}
				}
				break;
			}
			case (ConfigurationConversion.STRING): {
				switch (columnName) {
				case (ConfigurationConversion.STRING_MIN): {
					return LegendEntry.STRING_MINPRESENT;
				}
				case (ConfigurationConversion.STRING_MAX): {
					return LegendEntry.STRING_MAXPRESENT;
				}
				case (ConfigurationConversion.STRING_VALUES): {
					return LegendEntry.STRING_PRESENTSTRINGS;
				}
				}
				break;
			}
			case (ConfigurationConversion.CLASSES): {
				switch (columnName) {
				case (ConfigurationConversion.CLASSES_MIN): {
					return LegendEntry.CLASS_MININSTANCES;
				}
				case (ConfigurationConversion.CLASSES_MAX): {
					return LegendEntry.CLASS_MAXINSTANCES;
				}
				case (ConfigurationConversion.CLASSES_VALUES): {
					return LegendEntry.CLASS_INSTANCENAMES;
				}
				}
				break;
			}
			case (ConfigurationConversion.ATTRIBUTES): {
				switch (columnName) {
				case (ConfigurationConversion.ATTRIBUTES_MIN): {
					return LegendEntry.ATTRIBUTES_MINDEFINED;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAX): {
					return LegendEntry.ATTRIBUTES_MAXDEFINED;
				}
				case (ConfigurationConversion.ATTRIBUTES_MINSIZE): {
					return LegendEntry.ATTRIBUTES_MINELEMENTS;
				}
				case (ConfigurationConversion.ATTRIBUTES_MAXSIZE): {
					return LegendEntry.ATTRIBUTES_MAXELEMENTS;
				}
				case (ConfigurationConversion.ATTRIBUTES_VALUES): {
					return LegendEntry.ATTRIBUTES_ATTRIBUTEVALUES;
				}
				}
				break;
			}
			case (ConfigurationConversion.ASSOCIATIONS): {
				switch (columnName) {
				case (ConfigurationConversion.ASSOCIATIONS_MIN): {
					return LegendEntry.ASSOCIATIONS_MINLINKS;
				}
				case (ConfigurationConversion.ASSOCIATIONS_MAX): {
					return LegendEntry.ASSOCIATIONS_MAXLINKS;
				}
				case (ConfigurationConversion.ASSOCIATIONS_VALUES): {
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
