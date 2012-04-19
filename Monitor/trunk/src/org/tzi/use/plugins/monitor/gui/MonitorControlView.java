package org.tzi.use.plugins.monitor.gui;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.DefaultCheckboxTreeCellRenderer;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.monitor.AbstractMonitorStateListener;
import org.tzi.use.plugins.monitor.LogListener;
import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.plugins.monitor.MonitorStateListener;
import org.tzi.use.plugins.monitor.ProgressArgs;
import org.tzi.use.plugins.monitor.ProgressListener;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;
import org.tzi.use.util.StringUtil;

@SuppressWarnings("serial")
public class MonitorControlView extends JDialog implements StateChangeListener, ChangeListener, ProgressListener, LogListener {

	private Session session;
	
	private JButton button_Play;
	private JToggleButton button_Pause;
	private JButton button_Stop;
	
	private JTextField text_host;
	private JTextField text_port;
	private JCheckBox check_suspend;
	private JCheckBox check_determineStates;
	
	private JLabel label_useModel;
	
	private JProgressBar progressbar;
	private JLabel label_status;
	
	private CheckboxTree modelTree;
	
	private JTextPane logArea;
	private JCheckBox check_showDebugMessages;
	
	private MonitorStateListener stateChangeListener;
	
	public MonitorControlView(MainWindow parent, Session session) {
		super(parent, "Monitor Control");
		this.session = session;
		session.addChangeListener(this);
		
		initGUI();
		
		stateChangeListener = new AbstractMonitorStateListener() {
			/* (non-Javadoc)
			 * @see org.tzi.use.plugins.monitor.AbstractMonitorStateListener#monitorStateChanged(org.tzi.use.plugins.monitor.Monitor)
			 */
			@Override
			public void monitorStateChanged(Monitor source) {
				configureComponents();
			}
		};
		
		MonitorPlugin.getMonitorPluginInstance().getMonitor().addStateChangedListener(stateChangeListener);
		MonitorPlugin.getMonitorPluginInstance().getMonitor().addSnapshotProgressListener(this);
	}
	
	private void initGUI() {
		JPanel backPanel = new JPanel(new BorderLayout(3,2));
		this.getContentPane().add(backPanel);
		this.setMinimumSize(new Dimension(0, 0));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		backPanel.add(buttonPanel, BorderLayout.NORTH);
		
		URL iconUrl = MonitorPlugin.getInstance().getResource("resources/play.png");
		button_Play = new JButton("Connect", new ImageIcon(iconUrl));
		button_Play.setVerticalTextPosition(SwingConstants.BOTTOM);
		button_Play.setHorizontalTextPosition(SwingConstants.CENTER);
		button_Play.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MonitorSwingWorker worker = new MonitorSwingWorker() {
					@Override
					protected void doMonitorInBackground() {
						Map<String,String> monArgs = new HashMap<String, String>();
				    	monArgs.put("host", text_host.getText());
				    	monArgs.put("port", text_port.getText());
				    	
						try {
							MonitorPlugin
									.getMonitorPluginInstance()
									.getMonitor()
									.configure(session, new JVMAdapter(), monArgs);
						} catch (InvalidAdapterConfiguration e) {
							JOptionPane.showMessageDialog(
									MonitorControlView.this, e.getMessage(),
									"Invalid adapter configuration",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						MonitorPlugin.getMonitorPluginInstance().getMonitor()
								.start(check_suspend.isSelected());
						
						// If USE version < 3.1.0 psms are unknown
						if (check_determineStates != null
								&& check_suspend.isSelected()
								&& check_determineStates.isSelected())
							session.system()
									.state()
									.determineStates(
											new PrintWriter(new LogAreaWriter(
													"Determining states..."),
													true));
					}
					
				};
				worker.execute();
			}
		});
		buttonPanel.add(button_Play);

		iconUrl = MonitorPlugin.getInstance().getResource("resources/pause.png");
		button_Pause = new JToggleButton("Pause", new ImageIcon(iconUrl));
		button_Pause.setVerticalTextPosition(SwingConstants.BOTTOM);
		button_Pause.setHorizontalTextPosition(SwingConstants.CENTER);
		button_Pause.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MonitorSwingWorker worker = new MonitorSwingWorker() {
					@Override
					protected void doMonitorInBackground() {
						if (MonitorPlugin.getMonitorPluginInstance().getMonitor().isPaused())
							MonitorPlugin.getMonitorPluginInstance().getMonitor().resume();
						else {
							MonitorPlugin.getMonitorPluginInstance().getMonitor().pause();
							// If USE version < 3.1.0 psms are unknown
							if (check_determineStates != null
									&& check_determineStates.isSelected())
								session.system()
										.state()
										.determineStates(
												new PrintWriter(
														new LogAreaWriter(
																"Determining states..."),
														true));
						}
					}
				};
				worker.execute();
			}
		});
		buttonPanel.add(button_Pause);

		iconUrl = MonitorPlugin.getInstance().getResource("resources/stop.png");
		button_Stop = new JButton("Stop", new ImageIcon(iconUrl));
		button_Stop.setVerticalTextPosition(SwingConstants.BOTTOM);
		button_Stop.setHorizontalTextPosition(SwingConstants.CENTER);
		button_Stop.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MonitorPlugin.getMonitorPluginInstance().getMonitor().end();		
			}
		});
		buttonPanel.add(button_Stop);
		
		// Initialize settings tab
		JTabbedPane tabs = new JTabbedPane();
		backPanel.add(tabs, BorderLayout.CENTER);
		
		{
			JPanel tapPanel = new JPanel(new BorderLayout());
			JPanel info = new JPanel(new GridBagLayout());
			GridBagConstraints cLabel = new GridBagConstraints();
			GridBagConstraints cData = new GridBagConstraints();
			
			cLabel.gridx = 0;
			cLabel.gridy = 0;
			cLabel.anchor = GridBagConstraints.NORTHWEST;
			cLabel.fill = GridBagConstraints.NONE;
			cLabel.weightx = 0;
			cLabel.insets = new Insets(3, 3, 3, 3);
			cLabel.ipadx = 5;
			cLabel.ipady = 3;
						
			cData.gridx = 1;
			cData.gridy = 0;
			cData.anchor = GridBagConstraints.NORTHWEST;
			cData.fill = GridBagConstraints.HORIZONTAL;
			cData.weightx = 1;
			cData.insets = new Insets(3, 3, 3, 3);
			cData.ipadx = 5;
			cData.ipady = 3;
			
			info.add(new JLabel("USE model:"), cLabel);
			cLabel.gridy++;
			
			label_useModel = new JLabel(session.system().model().name());
			label_useModel.setFont(getFont().deriveFont(Font.BOLD));
			info.add(label_useModel, cData);
			cData.gridy++;
			
			info.add(new JLabel("Remote host:"), cLabel);
			cLabel.gridy++;
	
			text_host = new JTextField("localhost");
			info.add(text_host, cData);
			cData.gridy++;
	
			info.add(new JLabel("Port:"), cLabel);
			cLabel.gridy++;
	
			text_port = new JTextField("6000");
			info.add(text_port, cData);
			cData.gridy++;
	
			check_suspend = new JCheckBox("Suspend at connect", true);
			info.add(check_suspend, cData);
			cData.gridy++;
			
			// Determine correct USE version.
			// Cannot be accessed directly, because of optimizations
			String sVersion = "3.0.0";
			try {
				Field f = Options.class.getField("RELEASE_VERSION");
				Object version = f.get(null);
				sVersion = version.toString();
			} catch (Exception e1) {
				// Ignore exceptions during version determination.
			}
			
			if (sVersion.startsWith("3.0")) {
				check_determineStates = null;
			} else {
				check_determineStates = new JCheckBox("Determine states after suspend", true);
				info.add(check_determineStates, cData);
			}
			
			tapPanel.add(info, BorderLayout.NORTH);
			tabs.addTab("Settings", tapPanel);
		}
		
		{
			JPanel progress = new JPanel(new BorderLayout(3, 2));
			progressbar = new JProgressBar();
			progress.add(progressbar, BorderLayout.NORTH);
			label_status = new JLabel("Ready");
			progress.add(label_status, BorderLayout.SOUTH);
			backPanel.add(progress, BorderLayout.SOUTH);
		}
		
		// Initialize the "model tree" tab
		{
			JPanel modelPanel = new JPanel(new BorderLayout());
			DefaultMutableTreeNode root = createModelNodes();
			modelTree = new CheckboxTree(root);
			modelTree.setRootVisible(false);
			modelTree.setCellRenderer(new ModelCellRenderer());
			modelTree.getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK);
			
			modelPanel.add(new JScrollPane(modelTree), BorderLayout.CENTER);
			
			tabs.addTab("Model", modelPanel);
		}
		
		// Initialize the log tab
		{
			JPanel logPanel = new JPanel(new BorderLayout());
			
			JPanel logButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			logButtonsPanel.add(new JButton(new AbstractAction("Clear") {
				@Override
				public void actionPerformed(ActionEvent e) {
					logArea.setText("");
				}
			}));
			
			check_showDebugMessages = new JCheckBox("Show debug messages"); 
			logButtonsPanel.add(check_showDebugMessages);
			
			logPanel.add(logButtonsPanel, BorderLayout.NORTH);
			
			logArea = new JTextPane();
			logArea.setEditable(false);
			logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);	
			tabs.addTab("Log", logPanel);
			MonitorPlugin.getMonitorPluginInstance().getMonitor().addLogListener(this);
		}
		
		configureComponents();
		
		this.pack();
	}

	private DefaultMutableTreeNode createModelNodes() {
		ModelTreeNode<MModel> root = new ModelTreeNode<MModel>(false, session.system().model(), true);
		
		StringUtil.IElementFormatter<MOperation> opFormatter = new StringUtil.IElementFormatter<MOperation>() {
			@Override
			public String format(MOperation element) {
				return element.signature();
			}
		};
		
		// The default comparsion of MModelElement is compare the names
		SortedSet<MClass> sortedClasses = new TreeSet<MClass>(session.system().model().classes());
		
		for (MClass cls : sortedClasses) {
			ModelTreeNode<MClass> classNode = new ModelTreeNode<MClass>(false, cls, true); 
			root.add(classNode);

			SortedSet<MAttribute> attributes = new TreeSet<MAttribute>(cls.attributes());
			if (!attributes.isEmpty()) {
				ModelTreeNode<String> attributesNode = new ModelTreeNode<String>(false, "Attributes");
				
				classNode.add(attributesNode);
	
				for (MAttribute attr : attributes) {
					attributesNode.add(new ModelTreeNode<MAttribute>(true, attr));
				}
			}
			
			SortedSet<MOperation> operations = new TreeSet<MOperation>(cls.operations());
			if (!operations.isEmpty()) {
				ModelTreeNode<String> operationsNode = new ModelTreeNode<String>(false, "Operations");
				classNode.add(operationsNode);
	
				for (MOperation op : operations) {
					operationsNode.add(new ModelTreeNode<MOperation>(true, op, opFormatter));
				}
			}
		}
		
		for (MAssociation assoc : session.system().model().associations()) {
			ModelTreeNode<MAssociation> assocNode = new ModelTreeNode<MAssociation>(true, assoc, true); 
			root.add(assocNode);
		}
		
		return root;
	}
	
	private static class ModelTreeNode<T> extends DefaultMutableTreeNode {
		private StringUtil.IElementFormatter<T> formatter;
		private T userObject;
		private boolean isSelectable;
				
		public ModelTreeNode(boolean isSelectable, T userObject) {
			this(isSelectable, userObject, true, null);
		}
		
		public ModelTreeNode(boolean isSelectable, T userObject, StringUtil.IElementFormatter<T> formatter) {
			this(isSelectable, userObject, true, formatter);
		}
		
		public ModelTreeNode(boolean isSelectable, T userObject, boolean allowsChildren) {
			this(isSelectable, userObject, allowsChildren, null);
		}
		
		public ModelTreeNode(boolean isSelectable, T userObject, boolean allowsChildren, StringUtil.IElementFormatter<T> formatter) {
			super(userObject, allowsChildren);
			this.userObject = userObject;
			this.formatter = formatter;
			this.isSelectable = isSelectable;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
		 */
		@Override
		public String toString() {
			if (formatter != null)
				return formatter.format(userObject);
			
			return super.toString();
		}

		public boolean isSelectable() {
			return isSelectable;
		}
	}
	
	/**
     * This renderer shows different icons for the different user object types.
     */
    private static class ModelCellRenderer extends DefaultCheckboxTreeCellRenderer {
    	
    	private static Icon iconAssociation;
    	private static Icon iconClass;
    	private static Icon iconOperations;
    	private static Icon iconAttributes;

		static {
    		iconClass       =  new ImageIcon(MonitorPlugin.getInstance().getResource("resources/MClass.gif"));
    		iconAssociation =  new ImageIcon(MonitorPlugin.getInstance().getResource("resources/MAssociation.gif"));
    		iconOperations  =  new ImageIcon(MonitorPlugin.getInstance().getResource("resources/Operations.gif"));
    		iconAttributes  =  new ImageIcon(MonitorPlugin.getInstance().getResource("resources/Attributes.gif"));
    	}
    	
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			ModelTreeNode<?> node = (ModelTreeNode<?>) value;
			
			this.checkBox.setBackground(UIManager.getColor("Tree.textBackground"));
			
			Icon theIcon = getIconForModelObject(node.getUserObject()); 
			
			setClosedIcon(theIcon);
			setOpenIcon(theIcon);
			setLeafIcon(theIcon);
			
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			this.checkBox.setEnabled(node.isSelectable());
			
			return this;
		}

		private Icon getIconForModelObject(Object userObject) {
			if (userObject instanceof MClass) {
				return iconClass;
			} else if (userObject instanceof MAssociation) {
				return iconAssociation;
			} else if (userObject instanceof String) {
				if (userObject.toString().equals("Operations")) {
					return iconOperations;
				}
				if (userObject.toString().equals("Attributes")) {
					return iconAttributes;
				}
			}
			
			return null;
		}
    }

	private void configureComponents() {
		Monitor monitor = MonitorPlugin.getMonitorPluginInstance().getMonitor();
		boolean isMonitoring = monitor.isRunning();
		boolean isPaused = monitor.isPaused();
		
		this.text_host.setEnabled(!isMonitoring);
		this.text_port.setEnabled(!isMonitoring);
		this.check_suspend.setEnabled(!isMonitoring);
		
		if (this.check_determineStates != null)
			this.check_determineStates.setEnabled(!isMonitoring || !isPaused);
		
		this.button_Play.setEnabled(!isMonitoring);
		this.button_Pause.setEnabled(isMonitoring);
		this.button_Pause.setSelected(isPaused);
		this.button_Stop.setEnabled(isMonitoring);
	}
	
	@Override
	public void stateChanged(StateChangeEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		session.removeChangeListener(this);
		MonitorPlugin.getMonitorPluginInstance().getMonitor().removeStateChangedListener(stateChangeListener);
		MonitorPlugin.getMonitorPluginInstance().getMonitor().removeSnapshotProgressListener(this);
		MonitorPlugin.getMonitorPluginInstance().getMonitor().removeLogListener(this);
		super.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (session.hasSystem()) {
			this.label_useModel.setText(session.system().model().name());
			this.modelTree.setModel(new DefaultTreeModel(createModelNodes()));
		} else {
			this.label_useModel.setText("No USE file loaded");
			this.modelTree.setModel(null);
		}
	}

	@Override
	public void progressStart(ProgressArgs args) {
		this.progressbar.setMaximum(args.getEnd());
		this.progressbar.setMinimum(0);
		this.progressbar.setValue(0);
		this.progressbar.invalidate();
		
		this.label_status.setText(args.getDescription());
	}

	@Override
	public void progress(ProgressArgs args) {
		this.progressbar.setValue(args.getCurrent());
		
		this.label_status.setText(args.getDescription());
	}

	@Override
	public void progressEnd() {
		this.progressbar.setValue(0);
		this.progressbar.invalidate();
		
		this.label_status.setText("Ready");
	}

	protected abstract class MonitorSwingWorker extends SwingWorker<Void, Void> {
		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected final Void doInBackground() throws Exception {
			button_Pause.setEnabled(false);
			button_Play.setEnabled(false);
			button_Stop.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
			doMonitorInBackground();
			
			return null;
		}

		protected abstract void doMonitorInBackground();
		
		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		protected void done() {
			configureComponents();
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private SimpleAttributeSet textStyleDebug = new SimpleAttributeSet();
	private SimpleAttributeSet textStyleInfo = new SimpleAttributeSet();
	private SimpleAttributeSet textStyleWarning = new SimpleAttributeSet();
	private SimpleAttributeSet textStyleError = new SimpleAttributeSet();
	
	{
		StyleConstants.setForeground(textStyleDebug, Color.GRAY);
		StyleConstants.setForeground(textStyleInfo, new Color(0,127,14));
		StyleConstants.setForeground(textStyleWarning, new Color(255,106,0));
		StyleConstants.setForeground(textStyleError, Color.RED);
	}
	
	@Override
	public void newLogMessage(final Object source, final Level level, final String message) {
		if (level.intValue() < Level.INFO.intValue()
				&& !check_showDebugMessages.isSelected())
			return;
		
		final Date now = Calendar.getInstance().getTime();
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	addNewLogMessage(now, level, message);
            }
		});
	}
	
	
	private void addNewLogMessage(Date time, Level level, String message) {
		String toLog = String.format("%1$TT [%2$s]: %3$s", time, level, message);
		
		SimpleAttributeSet style;
		if (level.equals(Level.SEVERE))
			style = textStyleError;
		else if (level == Level.WARNING)
			style = textStyleWarning;
		else if (level == Level.INFO)
			style = textStyleInfo;
		else
			style = textStyleDebug;
				
		Document doc = logArea.getDocument();
		if (doc.getLength() > 0)
			toLog = StringUtil.NEWLINE + toLog;
		
		try {
			doc.insertString(doc.getLength(), toLog, style);
		} catch (BadLocationException e) { }
	}
	
	private class LogAreaWriter extends Writer {
		
		private boolean firstOutput = true;
				
		private final String infoMessage;
		
		private String buffer = "";
		
		public LogAreaWriter(String infoMessage) {
			this.infoMessage = infoMessage;
		}
		
		/* (non-Javadoc)
		 * @see java.io.Writer#write(char[], int, int)
		 */
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			final Date now = Calendar.getInstance().getTime();
			
			if (firstOutput) {
				// be safe here: we might be called from outside the
		        // event-dispatch thread
		        SwingUtilities.invokeLater(new Runnable() {
		                public void run() {
		                	addNewLogMessage(now, Level.INFO, infoMessage);
		                }
		        });	
				firstOutput = false;
			}
			
			String toLog = new String(cbuf, off, len);
			if (toLog.endsWith(StringUtil.NEWLINE)) {
				final String newLog = (buffer + toLog).replace(StringUtil.NEWLINE, "");
				buffer = "";
		        // be safe here: we might be called from outside the
		        // event-dispatch thread
		        SwingUtilities.invokeLater(new Runnable() {
		                public void run() {
		                	addNewLogMessage(now, Level.INFO, newLog);
		                }
		        });
		    } else {
		    	buffer += toLog;
		    }
		}

		/* (non-Javadoc)
		 * @see java.io.Writer#flush()
		 */
		@Override
		public void flush() throws IOException {
						
		}

		/* (non-Javadoc)
		 * @see java.io.Writer#close()
		 */
		@Override
		public void close() throws IOException {
						
		}
	}
}
