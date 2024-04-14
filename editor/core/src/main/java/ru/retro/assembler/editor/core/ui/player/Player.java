package ru.retro.assembler.editor.core.ui.player;

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

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.audio.AudioPlayer;
import ru.retro.assembler.editor.core.audio.AudioPlayerEvent;
import ru.retro.assembler.editor.core.audio.AudioPlayerException;
import ru.retro.assembler.editor.core.audio.SDLPlayer;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.ui.media.InteractivePanel;
import ru.retro.assembler.editor.core.util.ResourceUtils;

/**
 * @Author: Maxim Gorin Date: 21.03.2024
 */
@Slf4j
public class Player extends JDialog implements ModalDialog, AudioPlayerEvent {

    @Getter
    private InteractivePanel interactivePanel;

    private ButtonsPanel buttonsPanel;

    private AudioPlayer player;

    private double scale;

    private final byte[] sampleBuffer = new byte[4096];

    public Player(Frame owner) {
        this(owner, null);
        initComponents();
        initListeners();
    }

    public Player(Frame owner, AudioPlayer player) {
        super(owner);
        this.player = player;
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
        if (player == null) {
            player = new SDLPlayer(this);
        }
        buttonsPanel = new ButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
        interactivePanel = new InteractivePanel();
        add(interactivePanel, BorderLayout.CENTER);
        setResizable(false);
        buttonsPanel.getBtnPlay().setEnabled(false);
        buttonsPanel.getBtnStop().setEnabled(false);
        getRootPane().setDefaultButton(buttonsPanel.getBtnPlay());
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
        try {
            player.setFile(file);
            SwingUtilities.invokeLater(() -> {
                buttonsPanel.getBtnPlay().setEnabled(true);
                getRootPane().setDefaultButton(buttonsPanel.getBtnPlay());
                buttonsPanel.getBtnPlay().requestFocus();
                buttonsPanel.getBtnStop().setEnabled(false);
            });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            buttonsPanel.getBtnPlay().setEnabled(false);
            JOptionPane.showMessageDialog(Player.this.getOwner(), String.format(Messages
                            .getInstance().get(Messages.IO_ERROR), player.getFile().getAbsolutePath()),
                    Messages.getInstance().get(Messages
                            .ERROR), JOptionPane.ERROR_MESSAGE);

        } catch (AudioPlayerException e) {
            log.error(e.getMessage(), e);
            buttonsPanel.getBtnPlay().setEnabled(false);
            JOptionPane.showMessageDialog(Player.this.getOwner(), e.getMessage()
                    , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void play() {
        try {
            player.start();
        } catch (AudioPlayerException e) {
            JOptionPane.showMessageDialog(Player.this.getOwner(), e.getMessage()
                    , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void stop() {
        player.stop();
    }

    @Override
    public void received(AudioPlayer player, byte[] buf, int off, int len) {
        interactivePanel.getSlider().setValue((int) (player.getFramePosition() * scale));
        convertDataToLevel(buf, off, len);
    }

    private void convertDataToLevel(byte[] buf, int off, int len) {
        if (player.getFormat() != null && player.getFormat().getChannels() > 1) {
            log.info("Only mono mode supported");
            return;
        }
        switch (player.getFormat().getSampleSizeInBits()) {
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
    public void open(AudioPlayer player) {
        scale = (double) interactivePanel.getSlider().getMaximum() / player.getFrameLength();
    }

    @Override
    public void started(AudioPlayer player) {
        log.info("Player started");
        buttonsPanel.getBtnPlay().setEnabled(false);
        buttonsPanel.getBtnStop().setEnabled(true);
        getRootPane().setDefaultButton(buttonsPanel.getBtnStop());
        buttonsPanel.getBtnStop().requestFocus();
    }

    @Override
    public void stopped(AudioPlayer player) {
        log.info("Player stopped");
        interactivePanel.getSlider().setValue(0);
        buttonsPanel.getBtnPlay().setEnabled(true);
        getRootPane().setDefaultButton(buttonsPanel.getBtnPlay());
        buttonsPanel.getBtnPlay().requestFocus();
        buttonsPanel.getBtnStop().setEnabled(false);
        interactivePanel.reset();

    }

    @Override
    public void closed(AudioPlayer player) {
    }

    @Override
    public void error(Exception e) {
        JOptionPane.showMessageDialog(Player.this.getOwner(), e.getMessage()
                , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE);
    }
}

