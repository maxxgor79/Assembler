package ru.assembler.core.util;

import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
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

  public static BigInteger numberToInteger(@NonNull String s) {
    s = s.toLowerCase();
    int radix = 10;
    if (s.endsWith("h")) {
      s = s.substring(0, s.length() - 1);
      radix = 16;
    } else if (s.startsWith("0x")) {
      s = s.substring(2);
      radix = 16;
    } else if (s.endsWith("b")) {
      s = s.substring(0, s.length() - 1);
      radix = 2;
    } else if (s.startsWith("0b")) {
      s = s.substring(2);
      radix = 2;
    } else if (s.startsWith("0")) {
      s = s.substring(1);
      radix = 8;
    } else if (s.endsWith("q")) {
      s = s.substring(0, s.length() - 1);
      radix = 8;
    }
    return new BigInteger(s, radix);
  }
}
