package ru.retro.assembler.editor.core.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * ClipPlayer.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class ClipPlayer implements AudioPlayer, LineListener {

  private File file;

  private AudioState state = AudioState.Initialized;

  private AudioInputStream audioStream;

  private AudioFormat audioFormat;

  private AudioPlayerEvent handler;

  private Clip audioClip;

  public ClipPlayer(AudioPlayerEvent handler) {
    this.handler = handler;
  }

  @Override
  public void setFile(@NonNull File file) throws IOException, AudioPlayerException {
    this.file = file;
    try (FileInputStream fis = new FileInputStream(file)) {
      try {
        audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(IOUtils.toByteArray
            (fis)));
        DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.addLineListener(this);
        audioClip.open(audioStream);
      } catch (UnsupportedAudioFileException | LineUnavailableException e) {
        log.error(e.getMessage(), e);
        throw new AudioPlayerException(e);
      }
    }
    audioFormat = audioStream.getFormat();
  }

  @Override
  public File getFile() {
    return file;
  }

  @Override
  public void start() {
    audioClip.start();

  }

  @Override
  public void stop() {
    if (audioClip != null) {
      audioClip.stop();
      audioClip.setFramePosition(0);
    }
  }

  @Override
  public void close() {
    if (audioClip != null) {
      audioClip.close();
      audioClip = null;
    }
    IOUtils.closeQuietly(audioStream);
    audioStream = null;
    audioFormat = null;
  }

  @Override
  public AudioFormat getFormat() {
    return audioFormat;
  }

  @Override
  public AudioState getState() {
    return state;
  }

  @Override
  public long getFrameLength() {
    return audioClip == null ? 0 : audioClip.getFrameLength();
  }

  @Override
  public long getFramePosition() {
    return audioClip == null ? 0 : audioClip.getLongFramePosition();
  }

  @Override
  public void update(LineEvent e) {
    if (LineEvent.Type.OPEN == e.getType()) {
      log.info("Playback started.");
      state = AudioState.Open;
      if (handler != null) {
        handler.open(this);
      }
    } else if (LineEvent.Type.START == e.getType()) {
      log.info("Playback started.");
      state = AudioState.Started;
      if (handler != null) {
        handler.started(this);
      }
    } else if (LineEvent.Type.STOP == e.getType()) {
      log.info("Playback stopped.");
      state = AudioState.Stopped;
      if (handler != null) {
        handler.stopped(this);
      }
    } else if (LineEvent.Type.CLOSE == e.getType()) {
      log.info("Playback closed.");
      state = AudioState.Closed;
      if (handler != null) {
        handler.closed(this);
      }
    }
  }
}
