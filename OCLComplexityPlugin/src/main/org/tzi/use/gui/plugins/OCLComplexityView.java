package org.tzi.use.gui.plugins;

import org.tzi.use.OCLComplexity.Metric;
import org.tzi.use.OCLComplexity.MetricsCalculator;
import org.tzi.use.OCLComplexity.VisitorFactory;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.View;
import org.tzi.use.uml.mm.MModelElement;
import org.tzi.use.uml.sys.MSystem;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A view showing all expressions and their ocl complexity.
 *
 * @author Timo Stüber
 */
public class OCLComplexityView extends JPanel implements View {

    private final MSystem mSystem;

    // column names of the tables
    private ArrayList<String> columns;

    // table models
    private final OCLComplexityTableModel invModel;
    private final OCLComplexityTableModel preModel;
    private final OCLComplexityTableModel postModel;

    // this set contains all metrics. This can be used to get information like description / token.
    private final Set<Metric> metricSchema;

    // accumulation of the active tables
    private JTable totalTableFooter;

    // map tables to their listener
    private final Map<JTable, TableColumnModelListener> map;

    /**
     * This model is used to render a table header for accumulation of oclComplexityTableModels.
     * It is necessary to use a table header, because the we want columns of all tables should be synchronized.
     */
    public class FooterTableModel extends DefaultTableModel {
        // considered models
        private OCLComplexityTableModel[] models;
        // do not filter inactive models
        private boolean filterActive = true;

        public FooterTableModel(OCLComplexityTableModel[] models) {
            this.models = models;
        }

        public FooterTableModel(OCLComplexityTableModel[] models, boolean filterActive) {
            this.models = models;
            this.filterActive = filterActive;
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public int getRowCount() {
            // we just need the header for rendering no table data is shown
            return 0;
        }

        @Override
        public String getColumnName(int index) {
            if (index == 0) {
                // this is sigma and will be overwritten by an icon
                return "\u03A3";
            }
            /*
            // get active models, combine all expression (at first for the model then combine all models)
            Set<Metric> combinedMetric = Arrays.stream(models).filter(oclComplexityTableModel -> oclComplexityTableModel.isActive() || !filterActive)
                    .map(oclComplexityTableModel -> oclComplexityTableModel.data.values()
                            .stream().reduce(new HashSet<Metric>(), Metric::combine))
                    .reduce(new HashSet<Metric>(), Metric::combine);

            // return the sum for that column
            return String.valueOf(combinedMetric.stream().filter(
                    metric -> metric.getToken().equals(columns.get(index))).map(
                    Metric::getValue).reduce(0.0, Double::sum));
            */
            return String.valueOf(Arrays.stream(models).filter(oclComplexityTableModel -> oclComplexityTableModel.isActive() || !filterActive)
                    .map(oclComplexityTableModel -> oclComplexityTableModel.getTotalAt(index)).reduce(0.0, Double::sum));
        }
    }

    /**
     * Footer that accumulates the models.
     *
     * @param models
     * @return ScrollPane containing a Table.
     */
    private JScrollPane createTotalFooter(OCLComplexityTableModel[] models, boolean filterActive) {
        TableColumnModelListener footerListener = new ColumnChangeListener();
        JTable jTable = new JTable();
        jTable.getColumnModel().addColumnModelListener(footerListener);
        jTable.setModel(new FooterTableModel(models, filterActive));
        jTable.setAutoCreateRowSorter(false);
        jTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        jTable.getTableHeader().setResizingAllowed(false);
        jTable.getTableHeader().setReorderingAllowed(false);

        map.put(jTable, footerListener);
        JScrollPane tableFooterScrollPane = new JScrollPane(jTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // remove space below header; I do not know how this work and it took me a while to figure this fix out...
        tableFooterScrollPane.setMinimumSize(new Dimension(1,20));
        tableFooterScrollPane.invalidate();
        // remove table border
        tableFooterScrollPane.setBorder(BorderFactory.createEmptyBorder());
        return tableFooterScrollPane;
    }

    /**
     * Create a JTable with the OCLComplexityTableModel. The table added to a JScrollPane and returned.
     *
     * @param model
     * @return
     */
    private JScrollPane createTable(OCLComplexityTableModel model) {
        JTable jTable = new JTable();
        jTable.setAutoCreateRowSorter(true);
        jTable.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
        //jTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModelListener columnChangeListener = new ColumnChangeListener(jTable);
        jTable.getColumnModel().addColumnModelListener(columnChangeListener);
        jTable.setModel(model);

        map.put(jTable, columnChangeListener);

        final JScrollPane scrollPane = new JScrollPane(jTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    /**
     * Create a JCheckBox for that model. The checkbox changes the active state of the model.
     *
     * @param text
     * @param model
     * @return
     */
    private JCheckBox createCheckbox(String text, OCLComplexityTableModel model) {
        JCheckBox checkBox = new JCheckBox(text, true);
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                model.setActive(e.getStateChange() == ItemEvent.SELECTED);
                // repaint the tables
                map.keySet().stream().forEach(jTable -> ((AbstractTableModel) jTable.getModel()).fireTableStructureChanged());
            }
        });
        return checkBox;
    }

    private JButton createExportCSVButton() {
        ImageIcon export_icon = new ImageIcon(getClass().getResource("/resources/icon_download_01_black.png"));
        JButton exportButton = new JButton("Export CSV");
        exportButton.setIcon(export_icon);

        int margin_h = 10;
        BevelBorder border1 = new BevelBorder(0);
        EmptyBorder border2 = new EmptyBorder(1,margin_h,1,margin_h);
        Border newBorder = BorderFactory.createCompoundBorder(border1, border2);
        exportButton.setBorder(newBorder);

        exportButton.setFocusPainted(false);
        //exportButton.setBackground(Color.WHITE);

        exportButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFileChooser jFileChooser = new JFileChooser();
                FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
                jFileChooser.setFileFilter(fileNameExtensionFilter);

                jFileChooser.setDialogTitle("Export View as CSV");
                int returnValue = jFileChooser.showSaveDialog(OCLComplexityView.this);
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    String msg = "";
                    try {
                        File selectedFile = jFileChooser.getSelectedFile();
                        String fileName = selectedFile.getName();
                        // default absolute path (appended extension)
                        String absFileName = selectedFile.getAbsolutePath() + ".csv";

                        // check the extension
                        if(fileName.contains(".") && fileName.lastIndexOf(".") !=  0)
                        {
                            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                            // check if the extension is already csv then the filename is valid
                            if(fileExtension.equalsIgnoreCase("csv")) {
                                absFileName = selectedFile.getAbsolutePath();
                            }
                        }
                        exportViewToCSV(absFileName);
                        msg = "File saved successfully!";
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        msg = "Error while saving file: " + ex.getMessage();
                    }
                    JOptionPane.showMessageDialog(OCLComplexityView.this, msg);
                }
            }
        });
        return exportButton;
    }


    public OCLComplexityView(MainWindow parent, MSystem system) {
        super(new GridBagLayout());
        map = new HashMap<JTable, TableColumnModelListener>();
        mSystem = system;

        VisitorFactory visitorFactory = new VisitorFactory("COMPLEXITY");
        // get the metrics names
        metricSchema = visitorFactory.createVisitor(true).getMetrics();
        final MetricsCalculator metricsCalculator = new MetricsCalculator(visitorFactory);

        //set the models
        invModel = new OCLComplexityTableModel(metricsCalculator.calculateInvariantMetrics(mSystem.model(), true));
        preModel = new OCLComplexityTableModel(metricsCalculator.calculatePreConditionMetrics(mSystem.model(), true));
        postModel = new OCLComplexityTableModel(metricsCalculator.calculatePostConditionMetrics(mSystem.model(), true));

        // get metric names for the combobox
        ArrayList<String> metricNames = (ArrayList<String>) metricSchema.stream()
                .map(metric -> metric.getName()).collect(Collectors.toList());
        String comboBoxDefaultString = "Select metric...";
        metricNames.add(0, comboBoxDefaultString);

        // init the columns, First column is expression and the second is just last metric found.
        columns = new ArrayList<String>();
        columns.add("Expressions");
        String token = metricSchema.stream().filter(metric -> metric.getName().equals(metricNames.get(metricNames.size() - 1))).map(Metric::getToken).findFirst().get();
        columns.add(token);
        metricNames.remove(metricNames.size() - 1);

        // some constants for the layout
        int elementsPerGroup = 3;
        int invPosY = 2;
        int prePosY = invPosY + elementsPerGroup;
        int postPosY = prePosY + elementsPerGroup;

        // inv Table
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = invPosY;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridwidth = 3;

        // shrink the table if no rows are available
        double temp_weighty = gbc.weighty;
        double newWeight = 0.1;
        if(invModel.getRowCount() == 0) {
            gbc.weighty = newWeight;
        }
        add(createTable(invModel), gbc);
        gbc.weighty = temp_weighty;

        // pre Table
        if(preModel.getRowCount() == 0) {
            gbc.weighty = newWeight;
        }
        gbc.gridy = prePosY;
        add(createTable(preModel), gbc);
        gbc.weighty = temp_weighty;

        // post Table
        if(postModel.getRowCount() == 0) {
            gbc.weighty = newWeight;
        }
        gbc.gridy = postPosY;
        add(createTable(postModel), gbc);
        gbc.weighty = temp_weighty;

        // checkboxes
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        gbc.gridy = invPosY;
        add(createCheckbox("Invariants", invModel), gbc);

        gbc.gridy = prePosY;
        add(createCheckbox("PreConditions", preModel), gbc);

        gbc.gridy = postPosY;
        add(createCheckbox("PostConditions", postModel), gbc);

        gbc.gridy = 0;
        add(createExportCSVButton());

        // separators
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator invJSeparator = new JSeparator();
        JSeparator preJSeparator = new JSeparator();
        JSeparator postJSeparator = new JSeparator();
        JSeparator totalJSeparator = new JSeparator();
        gbc.gridy = invPosY - 1;
        add(invJSeparator, gbc);

        gbc.gridy = prePosY - 1;
        add(preJSeparator, gbc);

        gbc.gridy = postPosY - 1;
        add(postJSeparator, gbc);

        gbc.gridy = postPosY + 2;
        add(totalJSeparator, gbc);

        // combobox + label
        gbc.gridwidth = 1;
        gbc.gridy = 0;

        JLabel metricComboBoxLabel = new JLabel("Add Metric");
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.left = 5;
        gbc.insets.right = 10;
        add(metricComboBoxLabel, gbc);
        gbc.insets.right = 0;
        gbc.insets.left = 0;


        JComboBox<String> metricComboBox = new JComboBox<String>(metricNames.toArray(new String[0]));


        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(metricComboBox, gbc);

        // create Table Footer
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.gridy = invPosY + 1;
        add(createTotalFooter(new OCLComplexityTableModel[]{invModel}, false), gbc);

        gbc.gridy = prePosY + 1;
        add(createTotalFooter(new OCLComplexityTableModel[]{preModel}, false), gbc);

        gbc.gridy = postPosY + 1;
        add(createTotalFooter(new OCLComplexityTableModel[]{postModel}, false), gbc);

        gbc.gridy = postPosY + 3;
        JScrollPane totalPane = createTotalFooter(new OCLComplexityTableModel[]{invModel, preModel, postModel}, true);
        // this is necessary for repaint the footer
        totalTableFooter = (JTable) totalPane.getViewport().getComponent(0);
        add(totalPane, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.left = 5;
        JLabel totalTableFooterLabel = new JLabel("Total:");
        add(totalTableFooterLabel, gbc);

        metricComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                // add the selected item to the table and remove it from the combobox
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    String selectedMetric = (String) event.getItem();
                    if (!selectedMetric.equals(comboBoxDefaultString)) {
                        String token = metricSchema.stream().filter(metric -> metric.getName().equals(selectedMetric)).map(Metric::getToken).findFirst().get();
                        columns.add(token);
                        // remove the listener first, otherwise changing the selected item would cause a recursive loop
                        metricComboBox.removeItemListener(this);
                        metricComboBox.removeItemAt(metricComboBox.getSelectedIndex());
                        // 0 is the default index
                        metricComboBox.setSelectedIndex(0);
                        metricComboBox.addItemListener(this);

                        // repaint the the models
                        map.keySet().stream().forEach(jTable -> ((AbstractTableModel) jTable.getModel()).fireTableStructureChanged());
                    }
                }
            }
        });

        mSystem.getEventBus().register(this);
    }

    @Override
    public void detachModel() {

    }


    /**
     * We need this renderer for rendering the icons in the table header
     */
    class JComponentTableCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return (JComponent) value;
        }
    }

    /**
     * Listener for synchronize table columns and delegate mouse click events to the components of the header
     */
    class ColumnChangeListener implements TableColumnModelListener {
        private JTable jTable = null;

        /**
         * This constructor is for the footer
         */
        public ColumnChangeListener() {
        }

        /**
         * This constructor is needed for tables
         * @param jTable
         */
        public ColumnChangeListener(JTable jTable) {
            this.jTable = jTable;

            // mouse listener delegates the event to the component of the clicked column
            jTable.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    TableColumnModel model = ((JTableHeader) e.getSource()).getColumnModel();
                    int index = model.getColumnIndexAtX(e.getX());
                    TableColumn col = model.getColumn(index);
                    // delegate event to table headers
                    Arrays.stream(((JPanel) col.getHeaderValue()).getMouseListeners()).forEach(mouseListener -> mouseListener.mouseClicked(e));
                }
            });
        }

        /**
         * Whenever a column was added, the string is replaced by a JPanel including an icon for removing the element.
         * @param e
         */
        public void columnAdded(TableColumnModelEvent e) {

            TableColumnModel sourceModel = (TableColumnModel) e.getSource();
            // we only need to change the last column
            int num_columns = sourceModel.getColumnCount();
            TableColumn column = sourceModel.getColumn(num_columns - 1);
            // for rendering a jPanel
            TableCellRenderer renderer = new JComponentTableCellRenderer();
            column.setHeaderRenderer(renderer);

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets.left = 4;


            JPanel panel = new JPanel(new GridBagLayout());

            panel.setBorder(BorderFactory.createRaisedBevelBorder());

            // token of the metric
            String token = column.getHeaderValue().toString();
            // this is case for table footer
            if (jTable == null) {
                // column 1 is the sigma symbol and the the others are the sums of the tables
                if (num_columns == 1) {
                    ImageIcon sigma_icon = new ImageIcon(getClass().getResource("/resources/OCLComplexity_transparent.png"));
                    JLabel sigmaLabel = new JLabel(sigma_icon);
                    gridBagConstraints.fill = GridBagConstraints.NONE;
                    panel.add(sigmaLabel, gridBagConstraints);
                } else {
                    JLabel textLabel = new JLabel(token);
                    panel.add(textLabel, gridBagConstraints);
                }
                column.setHeaderValue(panel);
                return;
            }

            JLabel textLabel = new JLabel(token, JLabel.LEFT);

            panel.add(textLabel, gridBagConstraints);

            if (num_columns > 1) {
                // additional information for the tags
                String tooltip = metricSchema.stream().filter(metric -> metric.getToken().equals(token))
                        .map(metric -> metric.getName() + ": " + metric.getDescription()).findFirst().get();
                panel.setToolTipText(tooltip);

                // remove icon
                Icon icon = UIManager.getIcon("InternalFrame.closeIcon");
                JButton iconButton = new JButton(icon);
                iconButton.setBorder(BorderFactory.createEmptyBorder());

                // there should be at least one metric, which can not removed
                if (num_columns == 2) {
                    iconButton.setVisible(false);
                } else {
                    // make the remove_icon of the first metric entry visible, when more than two metrics are added
                    ((JPanel) sourceModel.getColumn(1).getHeaderValue()).getComponent(1).setVisible(true);
                }

                gridBagConstraints.gridx = 1;
                gridBagConstraints.weightx = 0.0;
                gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
                gridBagConstraints.insets.left = 0;
                gridBagConstraints.insets.right = 4;
                panel.add(iconButton,  gridBagConstraints);

                /**
                 * Unfortunately the panel cannot be added to the table header directly. It is rendered only.
                 * It is not possible to catch any event for the components. A mouse listener was added to the table header,
                 * which delegates the events to this the panel.
                 */
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        int width = 0;
                        TableColumn tb = null;
                        // iterate column until we found the current one and get the location of the current column.
                        // The event coordinates start at the beginning of the first column.
                        for (int i = 0; i < sourceModel.getColumnCount(); i++) {
                            tb = sourceModel.getColumn(i);
                            if (column.equals(tb)) {
                                break;
                            } else {
                                width += tb.getWidth();
                            }
                        }

                        boolean iconButtonClicked = iconButton.getBounds().contains(e.getX() - width, e.getY());

                        if (iconButtonClicked) {
                            String headerVal = textLabel.getText();
                            // remove metric
                            columns = (ArrayList<String>) columns.stream().filter(s -> !s.equals(headerVal)).collect(Collectors.toList());
                            if (columns.size() == 1) {
                                // make remove icon of the first metric entry invisible, when just one metrics is used
                                ((JPanel) sourceModel.getColumn(1).getHeaderValue()).getComponent(1).setVisible(false);
                            }
                            // repaint the columns
                            map.keySet().stream().forEach(table -> ((AbstractTableModel) table.getModel()).fireTableStructureChanged());
                        }
                    }
                });
            }

            column.setHeaderValue(panel);
        }

        public void columnSelectionChanged(ListSelectionEvent e) {}

        public void columnRemoved(TableColumnModelEvent e) {}

        public void columnMoved(TableColumnModelEvent e) {}


        /**
         * Synchronize the table columns width.
         * @param e
         */
        public void columnMarginChanged(ChangeEvent e) {
            TableColumnModel sourceModel = (TableColumnModel) e.getSource();
            TableColumnModelListener listener = null;
            TableColumnModel targetModel = null;


            // first remove the listener to prevent any recursion loop
            for (JTable table : map.keySet()) {
                targetModel = table.getColumnModel();
                listener = map.get(table);
                targetModel.removeColumnModelListener(listener);
            }


            for (JTable table : map.keySet()) {
                targetModel = table.getColumnModel();
                listener = map.get(table);

                for (int i = 0; i < sourceModel.getColumnCount(); i++) {
                    if (!sourceModel.equals(targetModel)) {
                        // FIXME: apparently the size of the synchronized table columns differs... this leads to some buggy behaviour (like flicker).
                        // I guess setPreferredWidth is not working well and causes a synchronize loop.
                        // we need to use both methods in order to reduce the flicker!
                        targetModel.getColumn(i).setPreferredWidth(sourceModel.getColumn(i).getWidth());
                        targetModel.getColumn(i).setWidth(sourceModel.getColumn(i).getWidth());
                    }
                }
            }

              // read the listener
            for (JTable table : map.keySet()) {
                targetModel = table.getColumnModel();
                listener = map.get(table);
                targetModel.addColumnModelListener(listener);
            }
        }
    }

    /**
     * The table model for showing the metrics.
     */
    class OCLComplexityTableModel extends AbstractTableModel {

        Map<MModelElement, Set<Metric>> data;
        // If this is set to true, the model is considered to calculation of the total metrics.
        private boolean active = true;

        OCLComplexityTableModel(Map<MModelElement, Set<Metric>> data) {
            this.data = data;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean state) {
            this.active = state;
        }

        @Override
        public String getColumnName(int col) {
            return columns.get(col);
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 0) {
                return String.class;
            }
            return Double.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                if (row < data.size()) {
                    // name of the expression
                    return ((MModelElement) data.keySet().toArray()[row]).name();
                }
            } else {
                if (row < data.size()) {
                    // value of the expression
                    return Metric.getFirstByToken((Set<Metric>) data.values().toArray()[row], columns.get(col)).getValue();
                }
            }
            // should never be reached
            return "error";
        }

        public double getTotalAt(int col) {
            // this are the expression names
            if (col == 0) {
                return 0;
            }
            Set<Metric> combinedMetric = data.values().stream().reduce(new HashSet<Metric>(), Metric::combine);
            return combinedMetric.stream().filter(
                    metric -> metric.getToken().equals(columns.get(col))).map(
                    Metric::getValue).reduce(0.0, Double::sum);
        }
    }

    private void exportViewToCSV(String filename) throws IOException {
        CSVBuilder builder = new CSVBuilder();
        Map<String, OCLComplexityTableModel> map = new LinkedHashMap<String, OCLComplexityTableModel>();
        map.put("Invariants", invModel);
        map.put("PreConditions", preModel);
        map.put("PostConditions", postModel);

        for(Map.Entry<String, OCLComplexityTableModel> entry : map.entrySet()) {
            builder.addColumn(entry.getKey());
            builder.addColumns(columns.subList(1, columns.size()));
            builder.addNewline();

            for(int row = 0 ; row < entry.getValue().getRowCount(); row++) {
                for(int col = 0 ; col < entry.getValue().getColumnCount(); col++) {
                    builder.addColumn(entry.getValue().getValueAt(row, col).toString());
                }
                builder.addNewline();
            }
            // add sum of each column
            for(int col = 0 ; col < entry.getValue().getColumnCount(); col++) {
                if(col == 0) {
                    builder.addColumn("Total");
                } else {
                    builder.addColumn(String.valueOf(entry.getValue().getTotalAt(col)));
                }
            }
            builder.addNewline();
            builder.addNewline();
        }
        builder.exportCSV(filename);
    }

    class CSVBuilder {
        private StringBuilder builder;
        private boolean needSeparator = false;
        private String separator = ",";
        private char decimalSeparator;


        public CSVBuilder() {
            builder = new StringBuilder();
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            // if decimal numbers are separated with comma then excel uses ; as csv separator
            decimalSeparator = decimalFormatSymbols.getDecimalSeparator();
            if(decimalSeparator == ',') {
                separator = ";";
            }
        }

        private void addSeparator() {
            if(needSeparator) {
                builder.append(separator);
            }
        }


        public void addColumn(String col) {
            addSeparator();
            // replace decimal separator with local separator
            // this is important when import the csv to excel...
            col = col.replace('.', decimalSeparator);
            builder.append(col);
            needSeparator = true;
        }

        public void addColumns(List<String> columns) {
            for(String str : columns) {
                addColumn(str);
            }
        }

        public void addNewline() {
            builder.append("\n");
            needSeparator = false;
        }

        public void exportCSV(String filename) throws IOException {
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.append(builder.toString());
            fileWriter.close();
        }

    }

}
