package org.tzi.use.kodkod.plugin.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.tzi.use.uml.mm.MAttribute;

public class AttributeTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<MVAttributeSettings> attributesSettings = Collections.emptyList();
	
	private static String[] columnNames = new String[] {
			ConfigurationConversion.ATTRIBUTES,
			ConfigurationConversion.ATTRIBUTES_MIN,
			ConfigurationConversion.ATTRIBUTES_MAX,
			ConfigurationConversion.ATTRIBUTES_MINSIZE,
			ConfigurationConversion.ATTRIBUTES_MAXSIZE,
			ConfigurationConversion.ATTRIBUTES_VALUES };
	
	public AttributeTableModel() {
	}
	
	/**
	 * @param cls
	 */
	public AttributeTableModel(MVClassSettings clsSettings) {
		super();
		this.attributesSettings = new ArrayList<>(clsSettings.getAttributeSettings().values());
	}

	@Override
	public int getRowCount() {
		
		/*if (this.cls.isPresent()) {
			return cls.get().allAttributes().size();
		} else {
			return 0;
		}
		
		return this.cls.isPresent() ? cls.get().allAttributes().size() : 0;
		*/
		
		return this.attributesSettings.size(); 
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column > 0;
	}

	@Override
	public Object getValueAt(int row, int column) {
		MVAttributeSettings settings = attributesSettings.get(row);
		
		switch(column) {
		case 0: 
			return settings.getAttribute().name();
		case 1:
			return settings.getValues();
		case 2:
			return settings.getDefiningInstances(); //TODO toString()-Methode ueberschreiben
		case 3:
			return settings.getCollectionSize();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MVAttributeSettings set = this.attributesSettings.get(row);
		
		switch (column) {
		case 1:
			List<String> values = (List<String>)aValue;
			set.setValues(values);
			break;

		default:
			break;
		}
	}

	public void setClass(MVClassSettings classSettings) {
		this.attributesSettings = new ArrayList<>(classSettings.getAttributeSettings().values());
		this.fireTableDataChanged();
	}
}
