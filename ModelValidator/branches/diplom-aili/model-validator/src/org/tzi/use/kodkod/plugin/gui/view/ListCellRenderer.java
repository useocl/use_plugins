package org.tzi.use.kodkod.plugin.gui.view;

import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.util.StringUtil;

public class ListCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public ListCellRenderer() { super(); }

    public void setValue(Object value) {
        List<?> values = (List<?>) value;
        setText((value == null) ? "" : StringUtil.fmtSeq(values, ","));
    }
}
