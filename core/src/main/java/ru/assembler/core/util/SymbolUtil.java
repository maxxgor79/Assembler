package ru.assembler.core.util;

import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
public final class SymbolUtil {
    private static final char EOL_SYMBOL = '\n';

    private static final String IGNORED_SYMBOLS = " \r\t\b\f";

    private SymbolUtil() {

    }

    public static boolean isSpace(int ch) {
        return IGNORED_SYMBOLS.indexOf(ch) >= 0;
    }

    public static boolean isLetter(int ch) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(ch) >= 0;
    }

    public static boolean isDecDigit(int ch) {
        return "0123456789".indexOf(ch) >= 0;
    }

    public static boolean isUnderline(int ch) {
        return ch == '_';
    }

    public static boolean isHexDigit(int ch) {
        if (isDecDigit(ch)) {
            return true;
        }
        return "abcdefABCDEF".indexOf(ch) >= 0;
    }

    public static boolean isOctalDigit(int ch) {
        return "01234567".indexOf(ch) >= 0;
    }

    public static boolean isBinaryDigit(int ch) {
        return "01".indexOf(ch) >= 0;
    }

    public static boolean isDelimiter(int ch) {
        return ",()+-%*/&|<>^~".indexOf(ch) >= 0;
    }

    public static boolean isEOL(int ch) {
        return ch == EOL_SYMBOL;
    }

    public static boolean isEOF(int ch) {
        return ch == -1;
    }

    public static boolean isComment(int ch) {
        return ch == ';';
    }

    public static boolean isHexNewStylePrefix(int ch) {
        return "xX".indexOf(ch) >= 0;
    }

    public static boolean isBinaryNewStylePrefix(int ch) {
        return "bB".indexOf(ch) >= 0;
    }

    public static boolean isHexOldStylePostfix(int ch) {
        return "hH".indexOf(ch) >= 0;
    }

    public static boolean isBinaryOldStylePostfix(int ch) {
        return "bB".indexOf(ch) >= 0;
    }

    public static boolean isOctalOldStylePostfix(int ch) {
        return "qQ".indexOf(ch) >= 0;
    }

    public static boolean isColon(int ch) {
        return ch == ':';
    }

    public static boolean isQuote(int ch) {
        return ch == '"';
    }

    public static boolean isApostrophe(int ch) {
        return ch == '\'';
    }

    public static boolean isDollar(int ch) {
        return ch == '$';
    }

    public static boolean isIdentifier(int ch) {
        return isLetter(ch) || isDecDigit(ch) || isUnderline(ch);
    }

    public static boolean isDot(int ch) {
        return ch == '.';
    }

    public static boolean isHash(int ch) {
        return ch == '#';
    }


    public static String fillChar(char c, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative");
        }
        StringBuilder sb = new StringBuilder();
        for (char i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String replace(@NonNull String s1, int index, @NonNull String s2) {
        if (index < 0) {
            throw new IllegalArgumentException("index is negative");
        }
        StringBuilder sb = new StringBuilder(s1);
        for (int i = 0; i < s2.length(); i++) {
            sb.setCharAt(i + index, s2.charAt(i));
        }
        return sb.toString();
    }

    public static Set<String> parseEnumeration(String s, @NonNull String delimiter) {
        if (s == null || s.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        String[] units = s.split(delimiter);
        for (String unit : units) {
            if (!unit.trim().isEmpty()) {
                result.add(unit);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
