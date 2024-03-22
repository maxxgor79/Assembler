package ru.retro.assembler.editor.core.audio;

/**
 * AudioDataReceiver.
 *
 * @author Maxim Gorin
 */
public interface AudioPlayerEvent {

  void received(AudioPlayer player, byte[] buf, int off, int len);

  void open(AudioPlayer player);

  void started(AudioPlayer player);

  void stopped(AudioPlayer player);

  void closed(AudioPlayer player);
}
