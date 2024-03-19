package ru.retro.assembler.editor.core.io;

/**
 * @Author: Maxim Gorin Date: 02.03.2024
 */
public class StoreException extends Exception {

  public StoreException(String message) {
    super(message);
  }

  public StoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public StoreException(Throwable cause) {
    super(cause);
  }
}
