package org.tzi.use.kodkod.plugin.gui.model;

/**
 * Interface designed for {@link JTable JTables} with tooltips per column.
 * 
 * @author Frank Hilken
 */
public interface TooltipTableModel {
	public String getColumnTooltip(int index);
}
