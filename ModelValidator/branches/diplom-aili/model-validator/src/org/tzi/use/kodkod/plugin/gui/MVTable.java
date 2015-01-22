package org.tzi.use.kodkod.plugin.gui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.tzi.use.kodkod.plugin.gui.model.TableModelAttribute;
import org.tzi.use.kodkod.plugin.gui.model.TableModelClass;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAbstractAssociationClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAbstractClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAssociation;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameAssociationClass;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameDerivedAttribute;
import org.tzi.use.kodkod.plugin.gui.view.RendererNameInheritedAttribute;
import org.tzi.use.kodkod.plugin.gui.view.RendererNonEditable;

public class MVTable extends JTable {
	private static final long serialVersionUID = 1L;

	public MVTable(TableModel model) {
		super(model);
	}

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
		} else {
			return super.getCellRenderer(row, column);
		}
	}

}
