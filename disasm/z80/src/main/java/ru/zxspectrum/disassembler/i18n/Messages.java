package ru.zxspectrum.disassembler.i18n;

import java.util.ResourceBundle;

/**
 * @author Maxim Gorin
 */
public final class Messages {

    private Messages() {

    }

    public static final String ERROR = "error";

    public static final String WARNING = "warning";

    public static final String PROGRAM_WELCOME = "programWelcome";

    public static final String WRITTEN_BY = "writtenBy";

    public static final String VARIABLE_PATTERNS_ARE_NOT_EQUAL = "variablePatternsAreNotEqual";

    public static final String INVALID_TABLE_FORMAT = "invalidLineFormat'";

    public static final String UNKNOWN_SYMBOL = "unknownSymbol";

    public static final String BAD_NUMBER_FORMAT = "badNumberFormat";

    public static final String CHARACTER_TOO_LONG = "characterTooLong";

    public static final String CHARACTER_NOT_CLOSED = "characterNotClosed";

    public static final String STRING_NOT_CLOSED = "stringNotClosed";

    public static final String HEXADECIMAL = "hexadecimal";

    public static final String OCTAL = "octal";

    public static final String BINARY = "binary";

    public static final String DECIMAL = "decimal";

    public static final String  UNKNOWN_COMMAND = "unknownCommand";

    public static final String TOTAL_ERRORS = "totalErrors";

    public static final String SUCCESSFULLY_DISASSEMBLED = "successfullyDisassembled";

    public static final String DISASSEMBLED_LINES_IN = "disassembledLinesIn";

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.Messages");

    public static String getMessage(String message) {
        if (message == null) {
            return null;
        }
        return resourceBundle.getString(message);
    }
}
