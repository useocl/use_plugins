package org.tzi.use.kodkod.plugin.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tzi.use.uml.mm.MModel;

public class ModelValidatorConfigurationWindow extends JDialog {
	
	private static final long serialVersionUID = 1L;

	public ModelValidatorConfigurationWindow(final JFrame parent, final MModel model) {
		super(parent, "Model-Validator Configuration");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL); // "blocks all top-level windows from the same Java application except those from its own child hierarchy"
		setResizable(false);
		
		getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		//GridBagConstraints gbc = new GridBagConstraints();
		
		mainPanel.add(new JLabel("Hello World"), getGBC(0, 0));

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
