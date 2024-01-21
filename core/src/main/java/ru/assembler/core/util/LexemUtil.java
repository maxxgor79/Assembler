package ru.assembler.core.util;

/**
 * @author Maxim Gorin
 */
public final class LexemUtil {

    private LexemUtil() {

    }

    public static boolean isHexNumber(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!SymbolUtil.isHexDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
