package ru.zxspectrum.disassembler.utils;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public final class SymbolUtils {
    private static final String HEX_DIGITS = "0123456789ABCDEF";

    private SymbolUtils() {

    }

    public static boolean isHexadecimalDigit(int ch) {
        return "abcdefABCDEF0123456789".indexOf(ch) >= 0;
    }

    public static boolean isDollar(int ch) {
        return ch == '$';
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

    public static boolean isPatternLetter(int ch) {
        return "nde".indexOf(ch) != -1;
    }

    public static String toHexString(int value) {
        value &= 0xFF;
        char hi = HEX_DIGITS.charAt((value >>> 4) & 0x0F);
        char lo = HEX_DIGITS.charAt(value & 0x0F);
        return new String(new char[]{hi, lo});
    }

    public static boolean isDot(@NonNull String s) {
        return ".".equals(s);
    }

    public static boolean isComma(@NonNull String s) {
        return ",".equals(s);
    }

    public static boolean isPlus(@NonNull String s) {
        return "+".equals(s);
    }

    public static boolean isMinus(@NonNull String s) {
        return "-".equals(s);
    }

    public static boolean isEqual(@NonNull String s) {
        return "=".equals(s);
    }

    public static boolean isBraceOpen(@NonNull String s) {
        return "(".equals(s);
    }
}
