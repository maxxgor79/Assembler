package ru.zxspectrum.assembler.util;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
public final class LexemUtil {
    private static final Set<String> KEYWORDS = new HashSet<>();

    private LexemUtil() {

    }

    public static boolean isKeyword(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return false;
        }
        return KEYWORDS.contains(identifier.toUpperCase());
    }

    public static boolean putKeyword(@NonNull String identifier) {
        return KEYWORDS.add(identifier.toUpperCase());
    }
}
