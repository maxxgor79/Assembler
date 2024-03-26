package ru.retro.assembler.editor.core.ui.record;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.audio.DTLRecorder;
import ru.retro.assembler.editor.core.audio.Recorder;
import ru.retro.assembler.editor.core.audio.RecorderEvent;
import ru.retro.assembler.editor.core.audio.RecorderException;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.ui.media.InteractivePanel;
import ru.retro.assembler.editor.core.util.ResourceUtils;

/**
 * Recorder.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class AudioRecorder extends JDialog implements ModalDialog, RecorderEvent {

  private InteractivePanel interactivePanel;

  private ButtonsPanel buttonsPanel;

  private Recorder recorder;

  private final byte[] sampleBuffer = new byte[4096];

  public AudioRecorder(final Frame owner) {
    this(owner, null);
    initComponents();
    initListeners();
  }

  public AudioRecorder(Frame owner, Recorder recorder) {
    super(owner);
    this.recorder = recorder;
    initComponents();
    initListeners();
  }

  private void initComponents() {
    setModal(true);
    setTitle(Messages.getInstance().get(Messages.RECORDER_TITLE));
    setSize(new Dimension(512, 200));
    setLayout(new BorderLayout());
    try {
      setIconImage(ResourceUtils.loadImage("/icon16x16/cassette.png"));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    if (recorder == null) {
      recorder = new DTLRecorder(this);
    }
    buttonsPanel = new ButtonsPanel();
    add(buttonsPanel, BorderLayout.SOUTH);
    interactivePanel = new InteractivePanel();
    interactivePanel.getSlider().setVisible(false);
    add(interactivePanel, BorderLayout.CENTER);
    setResizable(false);
    buttonsPanel.getBtnRecord().setEnabled(false);
    buttonsPanel.getBtnStop().setEnabled(false);
    getRootPane().setDefaultButton(buttonsPanel.getBtnRecord());
  }

  private void initListeners() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        recorder.close();
        dispose();
      }
    });
    buttonsPanel.getBtnRecord().addActionListener(e -> play());
    buttonsPanel.getBtnStop().addActionListener(e -> stop());
  }

  public void setFile(@NonNull File file) {
    try {
      recorder.setFile(file);
      buttonsPanel.getBtnRecord().setEnabled(true);
      buttonsPanel.getBtnRecord().requestFocus();
      buttonsPanel.getBtnStop().setEnabled(false);
    } catch (RecorderException | IOException e) {
      SwingUtilities.invokeLater(
          () -> JOptionPane.showMessageDialog(AudioRecorder.this.getOwner(), e.getMessage()
              , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE));
    }
  }


  protected void play() {
    try {
      recorder.start();
    } catch (RecorderException e) {
      SwingUtilities.invokeLater(
          () -> JOptionPane.showMessageDialog(AudioRecorder.this.getOwner(), e.getMessage()
              , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE));
    }
  }

  protected void stop() {
    recorder.stop();
  }

  private void convertDataToLevel(byte[] buf, int off, int len) {
    if (recorder.getFormat() != null && recorder.getFormat().getChannels() > 1) {
      log.info("Only mono mode supported");
      return;
    }
    switch (recorder.getFormat().getSampleSizeInBits()) {
      case 8:
        interactivePanel.updateData(buf, off, len);
        break;
      case 16:
        for (int j = 0, i = 0; i < len / 2; i++, j++) {
          sampleBuffer[j] = (byte) (buf[off + i * 2] + 0x80);
        }
        interactivePanel.updateData(sampleBuffer, 0, len / 2);
        break;
    }
  }

  @Override
  public int showModal() {
    setVisible(true);
    return 0;
  }

  @Override
  public void received(Recorder recorder, byte[] buf, int off, int len) {
    convertDataToLevel(buf, off, len);
  }

  @Override
  public void open(Recorder recorder) {

  }

  @Override
  public void started(Recorder recorder) {
    log.info("Recorder started");
    buttonsPanel.getBtnRecord().setEnabled(false);
    buttonsPanel.getBtnRecord().requestFocus();
    buttonsPanel.getBtnStop().setEnabled(true);
  }

  @Override
  public void stopped(Recorder recorder) {
    log.info("Recorder stopped");
    buttonsPanel.getBtnRecord().setEnabled(true);
    buttonsPanel.getBtnRecord().requestFocus();
    buttonsPanel.getBtnStop().setEnabled(false);
    interactivePanel.reset();
  }

  @Override
  public void closed(Recorder recorder) {

  }

  @Override
  public void error(Exception e) {
    SwingUtilities.invokeLater(
        () -> JOptionPane.showMessageDialog(AudioRecorder.this.getOwner(), e.getMessage()
            , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE));
  }
}
