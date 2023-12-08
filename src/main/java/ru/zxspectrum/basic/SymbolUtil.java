package ru.zxspectrum.basic;

public final class SymbolUtil {
    private SymbolUtil() {

    }

    public static boolean isWhite(int ch) {
        return " \t".indexOf(ch) != -1;
    }

    public static boolean isDigit(int ch) {
        return "01234567890".indexOf(ch) != -1;
    }

    public static boolean isAlphabet(int ch) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(ch) != -1;
    }

    public static boolean isOperator(int ch) {
        return ch >= 0xa5 && ch <= 0xff;
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

    public static boolean isDollar(int ch) {
        return ch == '$';
    }
}
