package ru.zxspectrum.assembler.util;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 *
 */
public final class Converter {
    private Converter() {

    }

    public static int binaryToInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 2);
    }

    public static BigInteger binaryToBigInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 2);
    }

    public static int octalToInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 8);
    }

    public static BigInteger octalToBigInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 8);
    }

    public static int decimalToInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 10);
    }

    public static BigInteger decimalToBigInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 10);
    }

    public static int hexadecimalToInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return Integer.parseInt(value, 16);
    }

    public static BigInteger hexadecimalToBiginteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        return new BigInteger(value, 16);
    }

    public static BigInteger charToBigInteger(String ch) {
        if (ch == null || ch.isEmpty()) {
            throw new IllegalArgumentException("ch is null or empty");
        }
        return BigInteger.valueOf(ch.charAt(0));
    }
}
