package ru.retro.assembler.editor.core.audio;

/**
 * AudioPlayerException.
 *
 * @author Maxim Gorin
 */
public class AudioPlayerException extends Exception {

  public AudioPlayerException() {
  }

  public AudioPlayerException(String message) {
    super(message);
  }

  public AudioPlayerException(String message, Throwable cause) {
    super(message, cause);
  }

  public AudioPlayerException(Throwable cause) {
    super(cause);
  }
}
