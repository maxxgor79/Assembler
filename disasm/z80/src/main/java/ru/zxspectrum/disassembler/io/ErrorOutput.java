package ru.zxspectrum.disassembler.io;

import lombok.NonNull;
import ru.zxspectrum.disassembler.i18n.Messages;

import java.io.File;

/**
 * ErrorOutput.
 *
 * @author Maxim Gorin
 */
public final class ErrorOutput {
    private static final String FORMAT1 = "[%s] %s: %s";

    private static final String FORMAT2 = "[%s]: %s";

    private static final String FORMAT3 = "[%s] %s [%d]: %s";

    private static final String FORMAT4 = "[%s] [%d]: %s";

    public static String formattedError(@NonNull final String sourceName, int line, @NonNull final String message
            , final Object... args) {
        final String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT3, error, sourceName, line, String.format(message, args));
    }

    public static String formattedError(int line, @NonNull final String message
            , final Object... args) {
        final String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT4, error, line, String.format(message, args));
    }

    public static String formattedError(@NonNull final String sourceName, @NonNull final String message
            , final Object... args) {
        final String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT1, error, sourceName, String.format(message, args));
    }

    public static String formattedError(@NonNull final File file, final String message, final Object... args) {
        return formattedError(file.getName(), message, args);
    }

    public static String formattedError(@NonNull final String message, final Object... args) {
        final String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT2, error, String.format(message, args));
    }

    public static void println(final File file, final String message, final Object... args) {
        final String s = formattedError(file, message, args);
        System.err.println(s);
    }

    public static void println(final String message, Object... args) {
        final String s = formattedError(message, args);
        System.err.println(s);
    }

    public static void print(File file, final String message, Object... args) {
        final String s = formattedError(file, message, args);
        System.err.print(s);
    }

    public static void print(final String message, Object... args) {
        final String s = formattedError(message, args);
        System.err.print(s);
    }
}
