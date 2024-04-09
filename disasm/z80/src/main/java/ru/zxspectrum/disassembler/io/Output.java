package ru.zxspectrum.disassembler.io;

import ru.zxspectrum.disassembler.i18n.Messages;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public class Output {    private static final String FORMAT2 = "%s : [%d] %s";

    private static final String FORMAT3 = "%s : %s";

    public static String formattedWarning(String sourceName, int line, String message, Object ... args) {
        String warning = Messages.getMessage(Messages.WARNING);
        return String.format(FORMAT2, warning, sourceName, line, String.format(message, args));
    }

    public static String formattedWarning(int line, String message, Object ... args) {
        String warning = Messages.getMessage(Messages.WARNING);
        return String.format(FORMAT2, warning, line, String.format(message, args));
    }

    public static String formattedWarning(String message, Object ... args) {
        String warning = Messages.getMessage(Messages.WARNING);
        return String.format(FORMAT3, warning, String.format(message, args));
    }

    public static void println(String format, Object ... args) {
        System.out.println(String.format(format, args));
    }

    public static void print(String format, Object ... args) {
        System.out.print(String.format(format, args));
    }

    public static void printlnWarning(String message, Object ... args) {
        System.out.println(formattedWarning(message, args));
    }

    public static void printWarning(String message, Object ... args) {
        System.out.print(formattedWarning(message, args));
    }

}
