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

    public static boolean isInRange(@NonNull final BigInteger minAddress, @NonNull final BigInteger maxAddress
            , @NonNull final BigInteger startAddress, @NonNull final BigInteger size) {
        final BigInteger endAddress = startAddress.add(size);
        if (startAddress.compareTo(minAddress) >= 0 && startAddress.compareTo(maxAddress) <= 0 &&
                endAddress.compareTo(minAddress) >= 0 && endAddress.compareTo(maxAddress) <= 0) {
            return true;
        }
        return false;
    }
}
