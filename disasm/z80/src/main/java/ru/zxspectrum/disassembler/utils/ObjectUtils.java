package ru.zxspectrum.disassembler.utils;

import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/4/2024
 */
public final class ObjectUtils {
    protected static final String LABEL_PREFIX = "lbl";

    private ObjectUtils() {

    }

    public static boolean isInRange(@NonNull BigInteger min, @NonNull BigInteger max, @NonNull BigInteger val) {
        return (min.compareTo(val) <= 0) && (max.compareTo(val) >= 0);
    }

    public static String generateLabelName(@NonNull BigInteger address, int addressSize) {
        return String.format("%s%0" + addressSize * 2 + "X", LABEL_PREFIX, address.longValue());
    }
}
