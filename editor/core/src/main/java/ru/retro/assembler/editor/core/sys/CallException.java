package ru.retro.assembler.editor.core.sys;

/**
 * CallException.
 *
 * @author Maxim Gorin
 */
public class CallException extends Exception{

  public CallException() {
  }

  public CallException(String message) {
    super(message);
  }

  public CallException(String message, Throwable cause) {
    super(message, cause);
  }

  public CallException(Throwable cause) {
    super(cause);
  }
}
