package org.tzi.use.kodkod.plugin.gui;

import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.util.StringUtil;

public class ListCellRenderer extends DefaultTableCellRenderer {

    public ListCellRenderer() { super(); }

    public void setValue(Object value) {
        List<?> values = (List<?>) value;
        setText((value == null) ? "" : StringUtil.fmtSeq(values, ","));
    }
}
