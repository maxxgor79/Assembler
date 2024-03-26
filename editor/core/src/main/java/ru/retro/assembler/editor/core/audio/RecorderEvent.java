package ru.retro.assembler.editor.core.audio;

/**
 * RecorderEvent.
 *
 * @author Maxim Gorin
 */
public interface RecorderEvent {

  void received(Recorder recorder, byte[] buf, int off, int len);

  void open(Recorder recorder);

  void started(Recorder recorder);

  void stopped(Recorder recorder);

  void closed(Recorder recorder);

  void error(Exception e);
}
