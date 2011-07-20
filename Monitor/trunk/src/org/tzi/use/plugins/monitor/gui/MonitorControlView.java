package org.tzi.use.plugins.monitor.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.monitor.AbstractMonitorStateListener;
import org.tzi.use.plugins.monitor.IMonitorStateListener;
import org.tzi.use.plugins.monitor.IProgressListener;
import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.plugins.monitor.MonitorPlugin;
import org.tzi.use.plugins.monitor.ProgressArgs;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;

import sun.awt.VerticalBagLayout;

@SuppressWarnings("serial")
public class MonitorControlView extends JDialog implements StateChangeListener, ChangeListener, IProgressListener {

	private Session session;
	
	private JButton button_Play;
	private JToggleButton button_Pause;
	private JButton button_Stop;
	
	private JTextField text_host;
	private JTextField text_port;
	private JCheckBox check_suspend;
	private JLabel label_useModel;
	
	private JProgressBar progressbar;
	private JLabel label_status;
	
	private IMonitorStateListener stateChangeListener;
	
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
		JPanel backPanel = new JPanel(new VerticalBagLayout());
		this.getContentPane().add(backPanel);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		backPanel.add(buttonPanel);
		
		URL iconUrl = MonitorPlugin.getInstance().getResource("resources/play.png");
		button_Play = new JButton("Play", new ImageIcon(iconUrl));
		button_Play.setVerticalTextPosition(SwingConstants.BOTTOM);
		button_Play.setHorizontalTextPosition(SwingConstants.CENTER);
		button_Play.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						MonitorControlView.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));

						MonitorPlugin
								.getMonitorPluginInstance()
								.getMonitor()
								.configure(session, text_host.getText(),
										text_port.getText());
						MonitorPlugin.getMonitorPluginInstance().getMonitor()
								.start(check_suspend.isSelected());

						MonitorControlView.this.setCursor(Cursor
								.getDefaultCursor());
					}
				});
				t.start();
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
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						MonitorControlView.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));

						if (MonitorPlugin.getMonitorPluginInstance()
								.getMonitor().isPaused())
							MonitorPlugin.getMonitorPluginInstance()
									.getMonitor().resume();
						else
							MonitorPlugin.getMonitorPluginInstance()
									.getMonitor().pause();

						MonitorControlView.this.setCursor(Cursor
								.getDefaultCursor());
					}
				});
				t.run();
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
		
		JPanel info = new JPanel(new GridBagLayout());
		info.setBorder(new TitledBorder("Information"));
		GridBagConstraints cLabel = new GridBagConstraints();
		GridBagConstraints cData = new GridBagConstraints();
		
		cLabel.gridx = 0;
		cLabel.gridy = 0;
		cLabel.anchor = GridBagConstraints.WEST;
		cLabel.fill = GridBagConstraints.NONE;
		cLabel.weightx = 0;
		cLabel.insets = new Insets(3, 3, 3, 3);
		cLabel.ipadx = 5;
		cLabel.ipady = 3;
		
		cData.gridx = 1;
		cData.gridy = 0;
		cData.anchor = GridBagConstraints.WEST;
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
		
		backPanel.add(info);
				
		JPanel progress = new JPanel(new BorderLayout(3, 2));
		progressbar = new JProgressBar();
		progress.add(progressbar, BorderLayout.NORTH);
		label_status = new JLabel("Ready");
		progress.add(label_status, BorderLayout.SOUTH);
		backPanel.add(progress);
		
		configureComponents();
		
		this.pack();
		this.setMinimumSize(this.getSize());
	}

	private void configureComponents() {
		Monitor monitor = MonitorPlugin.getMonitorPluginInstance().getMonitor();
		boolean isMonitoring = monitor.isRunning();
		boolean isPaused = monitor.isPaused();
		
		this.text_host.setEnabled(!isMonitoring);
		this.text_port.setEnabled(!isMonitoring);
		this.check_suspend.setEnabled(!isMonitoring);
		
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
		MonitorPlugin.getMonitorPluginInstance().getMonitor().removeStateChangedListener(stateChangeListener);
		MonitorPlugin.getMonitorPluginInstance().getMonitor().removeSnapshotProgressListener(this);
		super.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.label_useModel.setText(session.system().model().name());
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
}
