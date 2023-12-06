package ru.zxspectrum.basic;

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
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$".indexOf(ch) != -1;
    }

    public static boolean isDelimiter(int ch) {
        return "+-/*\'().,;=\u00c7\u00c8\u00c9".indexOf(ch) != -1;
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
}
