package ru.zxspectrum.assembler.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.lang.Type;

import java.math.BigInteger;

import static ru.zxspectrum.assembler.lang.Type.Unknown;

/**
 * @author Maxim Gorin
 */
@Slf4j
public final class TypeUtil {
    public static final char NUMBER_SYMBOL = 'n';

    public static final char OFFSET_SYMBOL = 'd';

    public static final char ADDRESS_OFFSET = 'e';

    private TypeUtil() {
    }

    private static int getRepeatedCount(@NonNull String mask, char ch) {
        if (mask.trim().isEmpty()) {
            throw new IllegalArgumentException("s is null or empty");
        }
        int count = 0;
        for (char c : mask.toCharArray()) {
            if (c == ch) {
                count++;
            } else {
                throw new IllegalArgumentException("Bad mask format: " + mask);
            }
        }
        return count;
    }

    public static Type toType(@NonNull String pattern) {
        if (pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("pattern is null or empty");
        }
        pattern = pattern.toLowerCase();
        if (pattern.startsWith(String.valueOf(NUMBER_SYMBOL))) {
            return Type.getUnsignedBySize(getRepeatedCount(pattern, NUMBER_SYMBOL));
        } else {
            if (pattern.startsWith(String.valueOf(OFFSET_SYMBOL))) {
                return Type.getSignedBySize(getRepeatedCount(pattern, OFFSET_SYMBOL));
            } else {
                if (pattern.startsWith(String.valueOf(ADDRESS_OFFSET))) {
                    return Type.getSignedBySize(getRepeatedCount(pattern, ADDRESS_OFFSET));
                }
            }
        }
        return Unknown;
    }

    public static boolean isAddressOffsetPattern(String pattern) {
        return isPattern(pattern, ADDRESS_OFFSET);
    }

    public static boolean isIntegerPattern(String pattern) {
        return isPattern(pattern, NUMBER_SYMBOL);
    }

    public static boolean isOffsetPattern(String pattern) {
        return isPattern(pattern, OFFSET_SYMBOL);
    }

    public static boolean isPatternSymbol(int ch) {
        if (ch == ADDRESS_OFFSET) {
            return true;
        }
        if (ch == OFFSET_SYMBOL) {
            return true;
        }
        return ch == NUMBER_SYMBOL;
    }

    private static boolean isPattern(@NonNull String pattern, char patternLetter) {
        String name = pattern.toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != patternLetter) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInRange(@NonNull Type type, @NonNull BigInteger value) {
        BigInteger min = BigInteger.valueOf(type.getMin());
        BigInteger max = BigInteger.valueOf(type.getMax());
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static boolean isInRange(@NonNull BigInteger min, @NonNull BigInteger max, @NonNull BigInteger value) {
        return (value.compareTo(min) >= 0) && (value.compareTo(max) <= 0);
    }

    public static boolean isInRange(String pattern, BigInteger value) {
        return isInRange(toType(pattern), value);
    }

    public static Type typeOf(@NonNull BigInteger val) {
        for (Type t : Type.values()) {
            if (val.compareTo(BigInteger.valueOf(t.getMin())) >= 0 &&
                    val.compareTo(BigInteger.valueOf(t.getMax())) <= 0) {
                return t;
            }
        }
        return null;
    }
}
