package ru.zxspectrum.assembler.util;

import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;
import ru.zxspectrum.assembler.lang.Type;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import static ru.zxspectrum.assembler.lang.Type.Int16;
import static ru.zxspectrum.assembler.lang.Type.Int32;
import static ru.zxspectrum.assembler.lang.Type.Int64;
import static ru.zxspectrum.assembler.lang.Type.Int8;
import static ru.zxspectrum.assembler.lang.Type.UInt16;
import static ru.zxspectrum.assembler.lang.Type.UInt32;
import static ru.zxspectrum.assembler.lang.Type.UInt64;
import static ru.zxspectrum.assembler.lang.Type.UInt8;
import static ru.zxspectrum.assembler.lang.Type.Unknown;

/**
 * @Author Maxim Gorin
 *
 */
public final class TypeUtil {
    private TypeUtil() {
    }

    public static int sizeof(Type t) {
        return switch (t) {
            case Int8 -> 1;
            case UInt8 -> 1;
            case Int16 -> 2;
            case UInt16 -> 2;
            case Int32 -> 4;
            case UInt32 -> 4;
            case Int64 -> 8;
            case UInt64 -> 8;
            default -> 0;
        };
    }

    private static int getRepeatedCount(String mask, char ch) {
        if (mask == null || mask.trim().isEmpty()) {
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

    public static Type toType(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("pattern is null or empty");
        }
        pattern = pattern.toLowerCase();
        if (pattern.startsWith(String.valueOf(getIntegerPatternSymbol()))) {
            switch (getRepeatedCount(pattern, getIntegerPatternSymbol())) {
                case 1:
                    return UInt8;
                case 2:
                    return UInt16;
                case 4:
                    return UInt32;
                case 8:
                    return UInt64;
            }
            ;
        } else {
            if (pattern.startsWith(String.valueOf(getOffsetPatternSymbol()))) {
                switch (getRepeatedCount(pattern, getOffsetPatternSymbol())) {
                    case 1:
                        return Int8;
                    case 2:
                        return Int16;
                    case 4:
                        return Int32;
                    case 8:
                        return Int64;
                }
            } else {
                if (pattern.startsWith(String.valueOf(getAddressPatternSymbol()))) {
                    switch (getRepeatedCount(pattern, getAddressPatternSymbol())) {
                        case 2:
                            return UInt16;
                        case 4:
                            return UInt32;
                        case 8:
                            return UInt64;
                    }
                } else {
                    if (pattern.startsWith(String.valueOf(getAddressOffsetPatternSymbol()))) {
                        switch (getRepeatedCount(pattern, getAddressOffsetPatternSymbol())) {
                            case 1:
                                return Int8;
                            case 2:
                                return Int16;
                            case 4:
                                return Int32;
                            case 8:
                                return Int64;
                        }
                    }
                }
            }
        }
        return Unknown;
    }

    public static byte[] toBytes(BigInteger value, Type type) {
        return toBytes(value, type, ByteOrder.LittleEndian);
    }

    public static byte[] toBytes(BigInteger value, Type type, ByteOrder byteOrder) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (byteOrder == ByteOrder.BigEndian) {
            return toBytesBigEndian(value, type);
        } else {
            return toBytesLittleEndian(value, type);
        }
    }

    private static byte[] toBytesBigEndian(BigInteger value, Type type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(new ByteArrayOutputStream());
        try {
            switch (type) {
                case Int8, UInt8 -> daos.writeByte(value.byteValue());
                case Int16, UInt16 -> daos.writeShort(value.shortValue());
                case Int32, UInt32 -> daos.writeInt(value.intValue());
                case Int64, UInt64 -> daos.writeLong(value.longValue());
                default -> throw new IllegalArgumentException("Bad type");
            }
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }

    private static byte[] toBytesLittleEndian(BigInteger value, Type type) {
        long longValue;
        switch (type) {
            case Int8, UInt8 -> longValue = value.byteValue();
            case Int16, UInt16 -> longValue = value.shortValue();
            case Int32, UInt32 -> longValue = value.intValue();
            case Int64, UInt64 -> longValue = value.longValue();
            default -> throw new IllegalArgumentException("Bad type");
        }
        int size = sizeof(type);
        byte byteData[] = new byte[size];
        for (int i = 0; i < size; i++) {
            byteData[i] = (byte) longValue;
            longValue = longValue >> 8;
        }
        return byteData;
    }

    public static char getOffsetPatternSymbol() {
        return 'd';
    }

    public static char getAddressOffsetPatternSymbol() {
        return 'o';
    }

    public static char getAddressPatternSymbol() {
        return 'a';
    }

    public static char getIntegerPatternSymbol() {
        return 'n';
    }

    public static boolean isAddressPattern(String pattern) {
        return isPattern(pattern, getAddressPatternSymbol());
    }

    public static boolean isAddressOffsetPattern(String pattern) {
        return isPattern(pattern, getAddressOffsetPatternSymbol());
    }

    public static boolean isIntegerPattern(String pattern) {
        return isPattern(pattern, getIntegerPatternSymbol());
    }

    public static boolean isOffsetPattern(String pattern) {
        return isPattern(pattern, getOffsetPatternSymbol());
    }

    public static boolean isPatternSymbol(int ch) {
        if (ch == getAddressOffsetPatternSymbol()) {
            return true;
        }
        if (ch == getAddressPatternSymbol()) {
            return true;
        }
        if (ch == getOffsetPatternSymbol()) {
            return true;
        }
        if (ch == getIntegerPatternSymbol()) {
            return true;
        }
        return false;
    }

    private static boolean isPattern(String pattern, char patternLetter) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        String name = pattern.toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != patternLetter) {
                return false;
            }
        }
        return true;
    }

    public static BigInteger getMin(Type type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        return switch (type) {
            case Int8 -> BigInteger.valueOf(-128L);
            case UInt8 -> BigInteger.ZERO;
            case Int16 -> BigInteger.valueOf(-32768L);
            case UInt16 -> BigInteger.ZERO;
            case Int32 -> BigInteger.valueOf(-2147483648L);
            case UInt32 -> BigInteger.ZERO;
            case Int64 -> new BigInteger("-9223372036854775808");
            case UInt64 -> BigInteger.ZERO;
            case Unknown -> null;
        };
    }

    public static BigInteger getMin(int size) {
        return switch (size) {
            case 8 -> BigInteger.ZERO;
            case 16 -> BigInteger.ZERO;
            case 32 -> BigInteger.ZERO;
            case 64 -> BigInteger.ZERO;
            default -> BigInteger.ZERO;
        };
    }

    public static BigInteger getMax(Type type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        return switch (type) {

            case Int8 -> BigInteger.valueOf(127L);
            case UInt8 -> BigInteger.valueOf(255L);
            case Int16 -> BigInteger.valueOf(32767L);
            case UInt16 -> BigInteger.valueOf(65535L);
            case Int32 -> BigInteger.valueOf(2147483647L);
            case UInt32 -> BigInteger.valueOf(4294967295L);
            case Int64 -> new BigInteger("9223372036854775807");
            case UInt64 -> new BigInteger("18446744073709551616");
            case Unknown -> null;
        };
    }

    public static BigInteger getMax(int size) {
        return switch (size) {
            case 8 -> BigInteger.valueOf(255);
            case 16 -> BigInteger.valueOf(65535);
            case 32 -> BigInteger.valueOf(4294967295L);
            case 64 -> new BigInteger("18446744073709551616");
            default -> BigInteger.ZERO;
        };
    }

    public static boolean isInRange(Type type, BigInteger value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        BigInteger min = getMin(type);
        BigInteger max = getMax(type);
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static boolean isInRange(BigInteger min, BigInteger max, BigInteger value) {
        if (min == null) {
            throw new NullPointerException("min");
        }
        if (max == null) {
            throw new NullPointerException("max");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        return (value.compareTo(min) >= 0) && (value.compareTo(max) <= 0);
    }

    public static boolean isInRange(String pattern, BigInteger value) {
        return isInRange(toType(pattern), value);
    }

    public static boolean isAddressPatternSymbol(int ch) {
        return getAddressPatternSymbol() == ch;
    }

    public static boolean isAddressOffsetPatternSymbol(int ch) {
        return getAddressOffsetPatternSymbol() == ch;
    }

    public static boolean isNumberPattenSymbol(int ch) {
        return getIntegerPatternSymbol() == ch;
    }

    public static boolean isOffsetPatternSymbol(int ch) {
        return getOffsetPatternSymbol() == ch;
    }
}
