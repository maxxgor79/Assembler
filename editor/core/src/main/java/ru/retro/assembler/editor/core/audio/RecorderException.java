package ru.retro.assembler.editor.core.audio;

/**
 * RecorderException.
 *
 * @author Maxim Gorin
 */
public class RecorderException extends Exception {

  public RecorderException() {
  }

  public RecorderException(String message) {
    super(message);
  }

  public RecorderException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecorderException(Throwable cause) {
    super(cause);
  }
}
