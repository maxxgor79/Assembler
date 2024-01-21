package ru.assembler.core.error.text;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public final class MessageFormatter {
    private MessageFormatter() {

    }

    public static String generateErrorText(File file, int lineNumber, String text, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(MessageList.getMessage(MessageList.ERROR)).append("]").append("%s:[%s] %s");
        String message = String.format(sb.toString(), file == null ? "" : file.getAbsolutePath()
                , Integer.valueOf(lineNumber), String.format(text, args));
        return message;
    }

    public static String generateWarningText(File file, int lineNumber, String text, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(MessageList.getMessage(MessageList.WARNING)).append("]").append("%s:[%s] %s");
        String message = String.format(sb.toString(), file == null ? "" : file.getAbsolutePath()
                , Integer.valueOf(lineNumber), String.format(text, args));
        return message;
    }
}

