package ru.retro.assembler.editor.core.audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

/**
 * Recorder.
 *
 * @author Maxim Gorin
 */
public interface Recorder {

  void setFile(File file) throws IOException, RecorderException;

  File getFile();

  void start() throws RecorderException;

  void stop();

  void close();

  AudioFormat getFormat();

  AudioState getState();

  long getFrameLength();
};
