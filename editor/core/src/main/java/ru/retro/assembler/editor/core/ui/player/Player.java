package ru.retro.assembler.editor.core.ui.player;

import javax.sound.sampled.LineEvent.Type;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.audio.AudioPlayer;
import ru.retro.assembler.editor.core.audio.AudioPlayerEvent;
import ru.retro.assembler.editor.core.audio.AudioPlayerException;
import ru.retro.assembler.editor.core.audio.ClipPlayer;
import ru.retro.assembler.editor.core.audio.SDLPlayer;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Maxim Gorin Date: 21.03.2024
 */
@Slf4j
public class Player extends JDialog implements ModalDialog, AudioPlayerEvent {

  private InteractivePanel interactivePanel;

  private ButtonsPanel buttonsPanel;

  private AudioPlayer player;
  private double scale;

  public Player(Frame owner) {
    super(owner);
    initComponents();
    initListeners();
  }

  private void initComponents() {
    setModal(true);
    setTitle(Messages.getInstance().get(Messages.PLAYER_TITLE));
    setSize(new Dimension(512, 200));
    setLayout(new BorderLayout());
    try {
      setIconImage(ResourceUtils.loadImage("/icon16x16/speaker.png"));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    //player = new SDLPlayer(this);
    player = new ClipPlayer(this);
    buttonsPanel = new ButtonsPanel();
    add(buttonsPanel, BorderLayout.SOUTH);
    interactivePanel = new InteractivePanel();
    add(interactivePanel, BorderLayout.CENTER);
    setResizable(false);
    buttonsPanel.getBtnPlay().setEnabled(false);
    buttonsPanel.getBtnStop().setEnabled(false);
  }

  private void initListeners() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        player.close();
        dispose();
      }
    });
    buttonsPanel.getBtnPlay().addActionListener(e -> play());
    buttonsPanel.getBtnStop().addActionListener(e -> stop());
  }

  public int showModal() {
    setVisible(true);
    return 0;
  }

  public void setFile(@NonNull File file) {
    buttonsPanel.getBtnPlay().setEnabled(false);
    try {
      player.setFile(file);
      buttonsPanel.getBtnPlay().setEnabled(true);
      buttonsPanel.getBtnStop().setEnabled(false);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      SwingUtilities.invokeLater(
          () -> JOptionPane.showMessageDialog(Player.this.getOwner(), String.format(Messages
                  .getInstance().get(Messages.IO_ERROR), player.getFile().getAbsolutePath()),
              Messages.getInstance().get(Messages
                  .ERROR), JOptionPane.ERROR_MESSAGE));
    } catch (AudioPlayerException e) {
      log.error(e.getMessage(), e);
      SwingUtilities.invokeLater(
          () -> JOptionPane.showMessageDialog(Player.this.getOwner(), e.getMessage()
              , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE));
    }
  }

  protected void play() {
    player.start();
  }

  protected void stop() {
    player.stop();
  }

  @Override
  public void received(AudioPlayer player, byte[] buf, int off, int len) {
    interactivePanel.getSlider().setValue((int) (player.getFramePosition() * scale));
  }

  @Override
  public void open(AudioPlayer player) {
    scale = (double) interactivePanel.getSlider().getMaximum() / player.getFrameLength();
  }

  @Override
  public void started(AudioPlayer player) {
    buttonsPanel.getBtnPlay().setEnabled(false);
    buttonsPanel.getBtnStop().setEnabled(true);
  }

  @Override
  public void stopped(AudioPlayer player) {
    interactivePanel.getSlider().setValue(0);
    buttonsPanel.getBtnPlay().setEnabled(true);
    buttonsPanel.getBtnStop().setEnabled(false);
  }

  @Override
  public void closed(AudioPlayer player) {
  }
}

