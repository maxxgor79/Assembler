package ru.zxspectrum.assembler.util;

import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 *
 */
public final class Converter {
    private Converter() {

    }

    public static int binaryToInt(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 2);
    }

    public static BigInteger binaryToBigInteger(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 2);
    }

    public static int octalToInt(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 8);
    }

    public static BigInteger octalToBigInteger(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 8);
    }

    public static int decimalToInt(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 10);
    }

    public static BigInteger decimalToBigInteger(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 10);
    }

    public static int hexadecimalToInt(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 16);
    }

    public static BigInteger hexadecimalToBiginteger(@NonNull String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 16);
    }

    public static BigInteger charToBigInteger(@NonNull String ch) {
        if (ch.isEmpty()) {
            throw new IllegalArgumentException("ch is null or empty");
        }
        return BigInteger.valueOf(ch.charAt(0));
    }
}
