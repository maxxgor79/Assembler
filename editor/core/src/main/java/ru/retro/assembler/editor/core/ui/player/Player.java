package ru.retro.assembler.editor.core.ui.player;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
 * @Author: Maxim Gorin
 * Date: 21.03.2024
 */
@Slf4j
public class Player extends JDialog implements ModalDialog, Runnable, LineListener {
    private InteractivePanel interactivePanel;

    private ButtonsPanel buttonsPanel;

    private File file;

    private AudioInputStream audioStream;

    private AudioFormat audioFormat;

    private SourceDataLine sourceDataLine;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final byte[] buffer = new byte[4096];

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
                stop();
                executorService.shutdownNow();
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
        this.file = file;
        if (file != null) {
            buttonsPanel.getBtnPlay().setEnabled(true);
            buttonsPanel.getBtnStop().setEnabled(false);
        } else {
            buttonsPanel.getBtnPlay().setEnabled(false);
            buttonsPanel.getBtnStop().setEnabled(false);
        }
    }

    protected void play() {
        if (file == null) {
            throw new IllegalArgumentException("file not defined");
        }
        try {
            audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(IOUtils.toByteArray
                    (new FileInputStream(file))));
            scale = (double) interactivePanel.getSlider().getMaximum() / audioStream.getFrameLength();
            audioFormat = audioStream.getFormat();
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.addLineListener(this);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            executorService.execute(this);
        } catch (LineUnavailableException | UnsupportedAudioFileException e) {
            log.error(e.getMessage(), e);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Player.this.getOwner(), e.getMessage()
                    , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Player.this.getOwner(), String.format(Messages
                    .getInstance().get(Messages.IO_ERROR), file.getAbsolutePath()), Messages.getInstance().get(Messages
                    .ERROR), JOptionPane.ERROR_MESSAGE));
        }
    }

    @Override
    public void run() {
        int readBytes;
        try {
            while ((readBytes = audioStream.read(buffer)) != -1) {
                sourceDataLine.write(buffer, 0, readBytes);
                receivedData(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        stop();
    }

    protected void stop() {
        if (sourceDataLine != null) {
            sourceDataLine.stop();
            sourceDataLine = null;
        }
    }

    protected void started() {
        buttonsPanel.getBtnPlay().setEnabled(false);
        buttonsPanel.getBtnStop().setEnabled(true);
    }

    protected void stopped() {
        interactivePanel.getSlider().setValue(0);
        buttonsPanel.getBtnPlay().setEnabled(true);
        buttonsPanel.getBtnStop().setEnabled(false);
        IOUtils.closeQuietly(audioStream);
        audioStream = null;
        audioFormat = null;
    }

    @Override
    public void update(LineEvent event) {
        if (LineEvent.Type.START == event.getType()) {
            log.info("Playback started.");
            started();
        } else if (LineEvent.Type.STOP == event.getType()) {
            log.info("Playback started.");
            stopped();
        }
    }

    protected void receivedData(byte[] buf, int off, int len) {
        interactivePanel.getSlider().setValue((int) (sourceDataLine.getLongFramePosition() * scale));
    }
}

