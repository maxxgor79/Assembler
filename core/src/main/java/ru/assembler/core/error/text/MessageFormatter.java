package ru.assembler.core.error.text;

import ru.assembler.core.io.FileDescriptor;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public final class MessageFormatter {
    private MessageFormatter() {

    }

    public static String generateErrorText(FileDescriptor fd, int lineNumber, String text, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(MessageList.getMessage(MessageList.ERROR)).append("] ").append("%s [%s]: %s");
        String message = String.format(sb.toString(), fd == null ? "" : fd.getDisplay()
                , Integer.valueOf(lineNumber), String.format(text, args));
        return message;
    }

    public static String generateWarningText(FileDescriptor fd, int lineNumber, String text, String... args) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[").append(MessageList.getMessage(MessageList.WARNING)).append("]").append("%s [%s]: %s");
        final String message = String.format(sb.toString(), fd.getDisplay() == null ? "" :
                        fd.getDisplay(), Integer.valueOf(lineNumber), String.format(text, args));
        return message;
    }
}

