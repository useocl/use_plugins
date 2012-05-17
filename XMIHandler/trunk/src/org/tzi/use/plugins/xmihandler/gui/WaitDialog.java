package org.tzi.use.plugins.xmihandler.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.plugins.xmihandler.utils.IWorkerRunner;
import org.tzi.use.plugins.xmihandler.utils.SwingWorker;

public class WaitDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  static int NUMLOOPS = 100;
  private SwingWorker worker;
  private MainWindow parent;

  public WaitDialog(JFrame parent, boolean modal, final IWorkerRunner runner) {
    super(parent, modal);
    setUndecorated(true);
    ((JPanel) getContentPane()).setBorder(BorderFactory.createLineBorder(
        Color.black, 1));
    initComponents();
    worker = new SwingWorker() {

      @Override
      public Object construct() {
        try {
          return runner.doWork();
        } catch (Exception ex) {
          ex.printStackTrace();
          return null;
        }
      }

      @Override
      public void finished() {
        runner.doUpdate();
        dispose();
      }
    };
    worker.start();
  }

  public WaitDialog(MainWindow parent, boolean modal) {
    super(parent, modal);
    initComponents();
    ((JPanel) getContentPane()).setBorder(BorderFactory.createLineBorder(
        Color.black, 1));
    pbProgress.setModel(new DefaultBoundedRangeModel(0, 0, 0, 100));
    this.parent = parent;
  }

  public void start(final IWorkerRunner runner) {
    worker = new SwingWorker() {

      @Override
      public Object construct() {
        try {
          return runner.doWork();
        } catch (Exception ex) {
          ex.printStackTrace();
          return null;
        }
      }

      @Override
      public void finished() {
        runner.doUpdate();
        dispose();
        parent.repaint();
      }
    };
    worker.start();
  }

  private void initComponents() {

    lbWorkTime = new javax.swing.JLabel();
    pbProgress = new javax.swing.JProgressBar();
    btnInterrupt = new javax.swing.JButton();
    lbSeconds = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setUndecorated(true);

    lbWorkTime.setText("Please wait...");

    pbProgress.setIndeterminate(true);

    btnInterrupt.setText("Cancel");
    btnInterrupt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnInterruptActionPerformed(evt);
      }
    });

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
        .addGroup(
            layout.createSequentialGroup().addContainerGap().addGroup(
                layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(
                        layout.createSequentialGroup().addComponent(lbWorkTime)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbSeconds)).addComponent(pbProgress,
                        GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(btnInterrupt, Alignment.TRAILING))
                .addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
        .addGroup(
            layout.createSequentialGroup().addContainerGap().addGroup(
                layout.createParallelGroup(Alignment.BASELINE).addComponent(
                    lbWorkTime).addComponent(lbSeconds)).addPreferredGap(
                LayoutStyle.ComponentPlacement.RELATED).addComponent(
                pbProgress, GroupLayout.PREFERRED_SIZE,
                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInterrupt).addContainerGap(
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    pack();
  }

  private void btnInterruptActionPerformed(ActionEvent evt) {
    int answer = JOptionPane.showConfirmDialog(this, "Are you sure to cancel?",
        "Waiting...", 0);
    if (answer == 0) {
      getWorker().interrupt();
      this.setVisible(false);
    }
  }

  public SwingWorker getWorker() {
    return worker;
  }

  private javax.swing.JButton btnInterrupt;
  private javax.swing.JLabel lbSeconds;
  private javax.swing.JLabel lbWorkTime;
  private javax.swing.JProgressBar pbProgress;
}
