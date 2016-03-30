package org.tzi.use.plugin.filmstrip.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class ProgressWindow extends JDialog implements WindowListener {

	private static final long serialVersionUID = 1L;
	
	private final JFrame owner;
	private final JProgressBar progressBar;
	
	public ProgressWindow(JFrame owner, String title){
		super(owner, title);
		this.owner = owner;
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);
		owner.setEnabled(false);
		
		setPreferredSize(new Dimension(250, 70));
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		panel.add(new JLabel("Processing model..."));
		
		progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(240, 25));
		panel.add(progressBar);
		
		setContentPane(panel);
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	public void done() {
		progressBar.setMaximum(1);
		progressBar.setValue(1);
		progressBar.setIndeterminate(false);
		progressBar.revalidate();
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		owner.setEnabled(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
