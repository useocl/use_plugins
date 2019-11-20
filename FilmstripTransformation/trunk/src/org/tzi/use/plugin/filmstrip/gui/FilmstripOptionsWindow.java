package org.tzi.use.plugin.filmstrip.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tzi.use.config.Options;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.plugin.filmstrip.logic.FilmstripMMVisitor;
import org.tzi.use.plugin.filmstrip.logic.FilmstripMVCOpCSnapAssocVisitor;
import org.tzi.use.plugin.filmstrip.logic.FilmstripMVCompatibleVisitor;
import org.tzi.use.plugin.filmstrip.logic.FilmstripOptions;
import org.tzi.use.plugin.filmstrip.logic.FilmstripTransformerTask;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.util.StringUtil;

public class FilmstripOptionsWindow extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField modelNameField;
	private JFileChooser filechooser;
	
	private JCheckBox boxCopy;
	private JCheckBox boxSoil;
	
	private JButton okButton;
	private JButton cancelButton;

	public FilmstripOptionsWindow(final JFrame parent, final MModel model) {
		super(parent, "Filmstrip Transformation Options");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		
		getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if((e.getSource() instanceof JComponent)
						&& ((JComponent) e.getSource()).getRootPane() == getRootPane()
						&& e.getID() == KeyEvent.KEY_PRESSED){
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						okButton.doClick();
						return true;
					}
					else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
						dispose();
						return true;
					}
				}
				return false;
			}
		});
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		int row = 0;
		
		mainPanel.add(new JLabel("Model name:"), getGBC(row, 0));
		
		modelNameField = new JTextField(model.name());
		mainPanel.add(modelNameField, getGBC(row, 1, 2, 1));
		row++;
		
		final JLabel fileLabel = new FilePathLabel();
		fileLabel.setPreferredSize(new Dimension(300, 20));
		JButton filechooserButton = new JButton("Select", new ImageIcon(Options.getIconPath("New.gif").toString()));
		filechooser = new JFileChooser(Options.getLastDirectory().toFile());
		filechooser.setFileFilter(new ExtFileFilter("use", "USE Model"));
		filechooser.setDialogTitle("Choose save file");
		filechooser.setMultiSelectionEnabled(false);
		filechooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.equals(JFileChooser.APPROVE_SELECTION)){
					fileLabel.setText(filechooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		filechooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filechooser.showSaveDialog(FilmstripOptionsWindow.this);
			}
		});
		
		mainPanel.add(new JLabel("Destination file:"), getGBC(row, 0));
		mainPanel.add(fileLabel, getGBC(row, 1));
		mainPanel.add(filechooserButton, getGBC(row, 2));
		row++;
		
		mainPanel.add(new JLabel("Transformation method:"), getGBC(row, 0));
		
		final JComboBox<String> transMethod = new JComboBox<String>(new String[]{ FilmstripMMVisitor.NAME, FilmstripMVCompatibleVisitor.NAME, FilmstripMVCOpCSnapAssocVisitor.NAME });
		transMethod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(FilmstripMMVisitor.NAME.equals(transMethod.getSelectedItem())){
					boxCopy.setEnabled(true);
					boxSoil.setEnabled(boxCopy.isSelected());
				}
				else {
					boxCopy.setEnabled(false);
					boxSoil.setEnabled(false);
				}
			}
		});
		mainPanel.add(transMethod, getGBC(row, 1, 2, 1));
		row++;
		
		JPanel addonPanel = new JPanel(new GridLayout(2, 1));
		addonPanel.setBorder(BorderFactory.createTitledBorder("Addons"));
		
		boxCopy = new JCheckBox("Create Snapshot::copy() operation");
		boxCopy.setSelected(true);
		boxCopy.setToolTipText("<html>Creates a copy operation to copy the latest snapshot creating its successor."
							+ "<br><i>Does not work with n-ary associations.</i></html>");
		addonPanel.add(boxCopy);
		
		boxSoil = new JCheckBox("Transform SOIL operations");
		boxSoil.setSelected(true);
		boxSoil.setToolTipText("<html>Makes imperative operations from the input model handle the snapshot creation automatically."
							+ "<br><i>Only available if the option Snapshot::copy() is chosen.</i></html>");
		addonPanel.add(boxSoil);
		
		boxCopy.addChangeListener(new ChangeListener() {
			/*
			 * boxSoil is dependent on boxCopy.
			 */
			private boolean savedState = boxSoil.isSelected();
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(boxSoil.isEnabled()){
					savedState = boxSoil.isSelected();
				}
				boxSoil.setEnabled(boxCopy.isSelected());
				if(boxCopy.isSelected()){
					boxSoil.setSelected(savedState);
				}
				else {
					boxSoil.setSelected(false);
				}
			}
		});
		mainPanel.add(addonPanel, getGBC(row, 0, 3, 1));
		row++;
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = filechooser.getSelectedFile();
				if(f == null){
					JOptionPane.showMessageDialog(FilmstripOptionsWindow.this,
							"Please select a file!", "No File", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(f.exists()){
					int overrideOption = JOptionPane
							.showConfirmDialog(FilmstripOptionsWindow.this,
									"Do you want to override the file "
											+ StringUtil.inQuotes(f.getName()) + "?",
									"File already exists",
									JOptionPane.YES_NO_OPTION);
					if(overrideOption != JOptionPane.YES_OPTION){
						return;
					}
				}
				
				String modelName = modelNameField.getText().trim();
				if(modelName.isEmpty()){
					JOptionPane.showMessageDialog(FilmstripOptionsWindow.this,
							"Please input a model name!", "No Model Name", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				dispose();
				FilmstripOptions options = new FilmstripOptions(model, modelName, f, boxCopy.isSelected(), boxSoil.isSelected(), (String) transMethod.getSelectedItem());
				ProgressWindow pw = new ProgressWindow(parent, "Please wait");
				FilmstripTransformerTask task = new FilmstripTransformerTask(options, pw);
				task.execute();
			}
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		buttonPanel.add(okButton, gbc);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(cancelButton, gbc);
		mainPanel.add(buttonPanel, getGBC(row, 0, 3, 1));
		
		setContentPane(mainPanel);
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private GridBagConstraints getGBC(int row, int col){
		return getGBC(row, col, 1, 1);
	}
	
	private GridBagConstraints getGBC(int row, int col, int gridWidth, int gridHeigh) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = gridWidth;
		gbc.gridheight = gridHeigh;
		gbc.insets.top = (row==0)?0:5;
		gbc.insets.left = (col>0)?5:0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		return gbc;
	}

}
