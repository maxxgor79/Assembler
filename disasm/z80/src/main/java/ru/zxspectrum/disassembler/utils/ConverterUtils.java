package ru.zxspectrum.disassembler.utils;

import lombok.NonNull;
import ru.zxspectrum.disassembler.decode.DecoderStrategy;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Maxim Gorin Date: 27.03.2024
 */
public final class ConverterUtils {

  private static final Map<Integer, String> ZERO_MAP = new HashMap<>();

  static {
    ZERO_MAP.put(0, "");
    ZERO_MAP.put(1, "0");
    ZERO_MAP.put(2, "");
    ZERO_MAP.put(3, "0");
    ZERO_MAP.put(4, "");
    ZERO_MAP.put(5, "000");
    ZERO_MAP.put(6, "00");
    ZERO_MAP.put(7, "0");
    ZERO_MAP.put(8, "");
  }

  private static final Map<String, Integer> radixName2Int = new HashMap<>();

  static {
    radixName2Int.put("bin", 2);
    radixName2Int.put("oct", 8);
    radixName2Int.put("dec", 10);
    radixName2Int.put("hex", 16);
  }

  private static final Map<String, Boolean> caseName2Boolean = new HashMap<>();

  static {
    caseName2Boolean.put("upper", true);
    caseName2Boolean.put("lower", false);
  }

  private static final Map<String, NumberStyle> styleName2NumberStyle = new HashMap<>();

  static {
    styleName2NumberStyle.put("c", NumberStyle.C);
    styleName2NumberStyle.put("java", NumberStyle.Java);
    styleName2NumberStyle.put("nix", NumberStyle.Nix);
    styleName2NumberStyle.put("classic", NumberStyle.Classic);
    styleName2NumberStyle.put("retro", NumberStyle.Retro);
  }

  private static final Map<String, DecoderStrategy> strategyName2DecoderStrategy = new HashMap<>();

  static {
    strategyName2DecoderStrategy.put("sequentially", DecoderStrategy.Sequentially);
    strategyName2DecoderStrategy.put("branching", DecoderStrategy.Branching);
  }

  private static final Map<String, Boolean> string2Boolean = new HashMap<>();

  static {
    string2Boolean.put("on", true);
    string2Boolean.put("off", false);
  }

  private ConverterUtils() {

  }

  public static DecoderStrategy toStrategy(@NonNull String name) {
    DecoderStrategy strategy = strategyName2DecoderStrategy.get(name);
    if (strategy == null) {
      return DecoderStrategy.Sequentially;
    }
    return strategy;
  }

  public static int toRadixIndex(@NonNull String radixName) {
    final Integer value = radixName2Int.get(radixName.toLowerCase());
    if (value == null) {
      return -1;
    }
    return value.intValue();
  }

  public static boolean toUpperCase(@NonNull String upperName) {
    final Boolean upperCase = caseName2Boolean.get(upperName);
    if (upperCase == null) {
      return false;
    }
    return upperCase.booleanValue();
  }

  private static String getSign(BigInteger value) {
    return value.signum() == -1 ? "-" : "";
  }

  private static String toHex(BigInteger value) {
    BigInteger result = value;
    if (value.signum() == -1) {
      result = value.negate();
    }
    final String s = (result.toString(16)).toLowerCase();
    return ZERO_MAP.get(s.length()) + s;
  }

  public static String toHex(@NonNull BigInteger value, @NonNull NumberStyle style) {
    return switch (style) {
      case Classic -> getSign(value) + toHex(value) + "h";
      case C, Java -> getSign(value) + "0x" + toHex(value);
      case Nix -> getSign(value) + "$" + toHex(value);
      case Retro -> getSign(value) + "#" + toHex(value);
    };
  }

  private static String toBinary(BigInteger value) {
    BigInteger result = value;
    if (value.signum() == -1) {
      result = value.negate();
    }
    return result.toString(2).toLowerCase();
  }

  public static String toBinary(@NonNull BigInteger value, @NonNull NumberStyle style) {
    return switch (style) {
      case Classic, Retro -> getSign(value) + toBinary(value) + "b";
      case C, Java, Nix -> getSign(value) + "0b" + toBinary(value);
    };
  }

  private static String toOctal(BigInteger value) {
    BigInteger result = value;
    if (value.signum() == -1) {
      result = value.negate();
    }
    return result.toString(8).toLowerCase();
  }

  public static String toOctal(@NonNull BigInteger value, @NonNull NumberStyle style) {
    return switch (style) {
      case Classic, Retro -> getSign(value) + toOctal(value) + "g";
      case C, Java, Nix -> getSign(value) + "0" + toOctal(value);
    };
  }

  public static String toDecimal(@NonNull BigInteger value, @NonNull NumberStyle style) {
    return value.toString(10);
  }

  public static NumberStyle toNumberStyle(@NonNull String name) {
    NumberStyle style = styleName2NumberStyle.get(name.toLowerCase());
    if (style == null) {
      style = NumberStyle.Classic;
    }
    return style;
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

  public static boolean getStringToBoolean(@NonNull String s) {
    s = s.toLowerCase();
    final Boolean val = string2Boolean.get(s);
    if (val == null) {
      return false;
    }
    return val.booleanValue();
  }
}
