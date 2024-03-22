package ru.retro.assembler.editor.core.audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

/**
 * AudioPlayer.
 *
 * @author Maxim Gorin
 */
public interface AudioPlayer {
  void setFile(File file) throws IOException, AudioPlayerException;

  File getFile();

  void start() throws AudioPlayerException;

  void stop();

  void close();

  AudioFormat getFormat();

  AudioState getState();

  long getFrameLength();

  long getFramePosition();
}
