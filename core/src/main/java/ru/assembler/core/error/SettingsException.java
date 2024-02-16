package ru.assembler.core.error;

/**
 * SettingsException.
 *
 * @author Maxim Gorin
 */
public class SettingsException extends Exception {

  public SettingsException(String message) {
    super(message);
  }

  public SettingsException(String message, Throwable cause) {
    super(message, cause);
  }

  public SettingsException(Throwable cause) {
    super(cause);
  }
}
