package ru.zxspectrum.basic;

/**
 * @author Maxim Gorin
 */
public final class SymbolUtil {
    private SymbolUtil() {

    }

    public static boolean isWhite(int ch) {
        return ch == ' ';
    }

    public static boolean isDigit(int ch) {
        return "01234567890".indexOf(ch) != -1;
    }

    public static boolean isAlphabet(int ch) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(ch) != -1;
    }

    public static boolean isUnderline(int ch) {
        return ch == '_';
    }

    public static boolean isOperator(int ch) {
        return (ch >= 0xa5 && ch <= 0xff) || (ch >= 0x10 && ch <= 0x17);
    }

    public static boolean isEol(int ch) {
        return ch == 0x0d;
    }

    public static boolean isQuote(int ch) {
        return ch == '"';
    }

    public static boolean isTextEol(int ch) {
        return ch == '\r' || ch == '\n';
    }

    public static boolean isTextWhite(int ch) {
        return " \t\f\u000b".indexOf(ch) != -1;
    }

    public static boolean isDollar(int ch) {
        return ch == '$';
    }
}
