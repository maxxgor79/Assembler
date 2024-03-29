package ru.zxspectrum.disassembler.io;

import ru.zxspectrum.disassembler.i18n.Messages;

/**
 * ErrorOutput.
 *
 * @author Maxim Gorin
 */
public final class ErrorOutput {
    private static final String FORMAT1 = "%s %s: [%d] %s";

    private static final String FORMAT2 = "%s : [%d] %s";

    private static final String FORMAT3 = "%s : %s";

    public static String formattedError(String sourceName, int line, String message, Object ... args) {
      final String error = Messages.getMessage(Messages.ERROR);
      return String.format(FORMAT1, error, sourceName, line, String.format(message, args));
    }

    public static String formattedError(int line, String message, Object ... args) {
      final String error = Messages.getMessage(Messages.ERROR);
      return String.format(FORMAT2, error, line, String.format(message, args));
    }

    public static String formattedError(String message, Object ... args) {
      final String error = Messages.getMessage(Messages.ERROR);
      return String.format(FORMAT3, error, String.format(message, args));
    }

    public static void println(final String message, String ... args) {
      System.err.println(formattedError(message, args));
    }

    public static void print(final String message, String ... args) {
      System.err.print(formattedError(message, args));
    }
}
