package ru.zxspectrum.disassembler.utils;

import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author maxim
 * Date: 1/4/2024
 */
public final class ObjectUtils {
    private ObjectUtils() {

    }

    public static boolean isInRange(@NonNull BigInteger min, @NonNull BigInteger max, @NonNull BigInteger val) {
        return (min.compareTo(val) <= 0) && (max.compareTo(val) >= 0);
    }
}
