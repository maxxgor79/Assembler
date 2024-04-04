package ru.retro.assembler.editor.core.ui.progress;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SimpleWorker<T> extends SwingWorker<T, Void> {
  private final CountDownLatch latch = new CountDownLatch(1);

  private static class GraphicsWorker extends JDialog {

    private final JProgressBar progressResponse = new JProgressBar();

    public GraphicsWorker(Window frame, String title, boolean modal) {
      super(frame, title);
      setModal(modal);
      initComponents();
    }

    public GraphicsWorker() {
      this(null, "", true);
    }

    private void initComponents() {
      setUndecorated(true);
      progressResponse.setIndeterminate(true);
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder(Color.white, new Color(142, 142, 142)));
      GridBagLayout gb = new GridBagLayout();
      panel.setLayout(gb);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.weighty = 1.0;
      gbc.weightx = 1.0;
      gbc.insets = new Insets(2, 2, 2, 2);
      gbc.anchor = GridBagConstraints.CENTER;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gb.setConstraints(progressResponse, gbc);
      panel.add(progressResponse);
      this.setLayout(new BorderLayout());
      this.add(panel, BorderLayout.CENTER);
      pack();
    }
  }

  private final GraphicsWorker graphicsWorker;
  private final Window frame;
  private T result;

  public SimpleWorker(Window frame) {
    this.frame = frame;
    graphicsWorker = new GraphicsWorker(frame, null, true);
    graphicsWorker.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        latch.countDown();
      }
    });
  }

  @Override
  protected T doInBackground() throws Exception {
    showWaiter();
    try {
      latch.await();
      return result = perform();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    } finally {
      hideWaiter();
    }
  }

  private void showWaiter() {
    SwingUtilities.invokeLater(() -> {
      graphicsWorker.setLocationRelativeTo(frame);
      graphicsWorker.setModal(true);
      graphicsWorker.setVisible(true);
    });
  }

  private void hideWaiter() {
    SwingUtilities.invokeLater(() -> {
      graphicsWorker.setVisible(false);
      graphicsWorker.dispose();
    });
  }

  @Override
  final protected void done() {
    try {
      done(result);
    } finally {
      this.result = null;
    }
  }

  protected void done(T result) {
  }

  protected abstract T perform() throws Exception;
}
