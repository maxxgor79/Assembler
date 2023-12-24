package ru.zxspectrum.assembler.util;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

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
