package ru.retro.assembler.editor.core.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * DTLRecorder.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class DTLRecorder implements Recorder, LineListener {

  private File file;

  private AudioFormat audioFormat;

  private AudioState state = AudioState.Initialized;

  private Thread thread;

  private RecorderEvent handler;

  private final byte[] buffer = new byte[4096];

  private TargetDataLine targetLine;

  private long totalRead;

  public DTLRecorder(@NonNull RecorderEvent handler) {
    this.handler = handler;
    this.audioFormat = new AudioFormat(22050, 8, 1, false
        , false);
  }

  @Override
  public void setFile(@NonNull File file) throws IOException, RecorderException {
    this.file = file;
  }

  @Override
  public File getFile() {
    return file;
  }

  @Override
  public void start() throws RecorderException {
    if (file == null) {
      throw new IllegalArgumentException("file is null");
    }
    if (thread != null) {
      throw new IllegalStateException("Recorder is running");
    }
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
          targetLine = (TargetDataLine) AudioSystem.getLine(info);
          targetLine.addLineListener(DTLRecorder.this);
          targetLine.open(audioFormat);
          targetLine.start();
          totalRead = 0;
          final ByteArrayOutputStream baos = new ByteArrayOutputStream();
          while (!thread.isInterrupted()) {
            int availableBytes = targetLine.available();
            if (availableBytes > 0) {
              if (availableBytes > buffer.length) {
                availableBytes = buffer.length;
              }
              targetLine.read(buffer, 0, availableBytes);
              baos.write(buffer, 0, availableBytes);
              totalRead += availableBytes;
              if (handler != null) {
                handler.received(DTLRecorder.this, buffer, 0, availableBytes);
              }
            }
          }
          final AudioInputStream ais = new AudioInputStream(
              new ByteArrayInputStream(baos.toByteArray())
              , audioFormat, getFrameLength());
          AudioSystem.write(ais, Type.WAVE, file);
        } catch (LineUnavailableException | IOException e) {
          log.error(e.getMessage(), e);
          if (handler != null) {
            handler.error(e);
          }
        } finally {
          close();
          thread = null;
          targetLine = null;
        }
        log.info("Stopped");
      }
    });
    thread.start();
  }

  @Override
  public void stop() {
    log.info("Stop");
    close();
  }

  @Override
  public void close() {
    log.info("Close");
    if (thread != null) {
      thread.interrupt();
    }
    if (targetLine != null) {
      targetLine.stop();
      targetLine.close();
    }
    state = AudioState.Initialized;
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
    long frames = totalRead;
    if (audioFormat != null) {
      frames /= audioFormat.getChannels();
      frames /= audioFormat.getSampleSizeInBits() / 8;
    }
    return frames;
  }

  @Override
  public void update(LineEvent e) {
    if (LineEvent.Type.OPEN == e.getType()) {
      log.info("Recording started.");
      state = AudioState.Open;
      if (handler != null) {
        handler.open(this);
      }
    } else if (LineEvent.Type.START == e.getType()) {
      log.info("Recording started.");
      state = AudioState.Started;
      if (handler != null) {
        handler.started(this);
      }
    } else if (LineEvent.Type.STOP == e.getType()) {
      log.info("Recording stopped.");
      state = AudioState.Stopped;
      if (handler != null) {
        handler.stopped(this);
      }
    } else if (LineEvent.Type.CLOSE == e.getType()) {
      log.info("Recording closed.");
      state = AudioState.Closed;
      if (handler != null) {
        handler.closed(this);
      }
    }
  }
}
