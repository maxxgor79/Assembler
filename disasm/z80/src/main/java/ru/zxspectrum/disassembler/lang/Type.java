package ru.zxspectrum.disassembler.lang;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
public enum Type {
    Int8(1, -128, 127, new String[]{"d", "e"}), UInt8(1, 0, 255, new String[]{"n"}), Int16(2, -32768, 32767, new String[]{"dd", "ee"}), UInt16(2, 0, 65535
            , new String[]{"nn"}), Int32(4, Integer.MIN_VALUE, Integer.MAX_VALUE
            , new String[]{"dddd", "eeee"}), UInt32(4, 0, 0xFFFFFFFF, new String[]{"nnnn"}), Unknown(0, 0, 0, new String[0]);

    Type(int size, long min, long max, String[] patterns) {
        this.size = size;
        this.min = min;
        this.max = max;
        this.patterns = patterns;
    }

    public String[] getPatterns() {
        return patterns;
    }

    private byte[] getByte(byte val) {
        byte[] a = new byte[1];
        a[0] = val;
        return a;
    }

    private byte[] getLEShort(short val) {
        byte[] a = new byte[2];
        a[0] = (byte) val;
        a[1] = (byte) (val >>> 8);
        return a;
    }

    private byte[] getLEInt(int val) {
        byte[] a = new byte[4];
        a[0] = (byte) val;
        a[1] = (byte) (val >>> 8);
        a[2] = (byte) (val >>> 16);
        a[3] = (byte) (val >>> 24);
        return a;
    }

    private byte[] getBEShort(short val) {
        byte[] a = new byte[2];
        a[1] = (byte) val;
        a[0] = (byte) (val >>> 8);
        return a;
    }

    private byte[] getBEInt(int val) {
        byte[] a = new byte[4];
        a[3] = (byte) val;
        a[2] = (byte) (val >>> 8);
        a[1] = (byte) (val >>> 16);
        a[0] = (byte) (val >>> 24);
        return a;
    }

    public byte[] getBytes(@NonNull BigInteger value, @NonNull ByteOrder order) {
        int result;
        if (order == ByteOrder.LittleEndian) {
            switch (this) {
                case Int8, UInt8 -> {
                    return getByte(value.byteValue());
                }
                case Int16, UInt16 -> {
                    return getLEShort(value.shortValue());
                }
                case Int32, UInt32 -> {
                    return getLEInt(value.shortValue());
                }
            }
        } else {
            switch (this) {
                case Int8, UInt8 -> {
                    return getByte(value.byteValue());
                }
                case Int16, UInt16 -> {
                    return getBEShort(value.shortValue());
                }

                case Int32, UInt32 -> {
                    return getBEInt(value.intValue());
                }
            }
        }
        throw new NumberFormatException();
    }

    public static Type getByPattern(@NonNull String pattern) {
        if (!pattern.trim().isEmpty()) {
            for (Type t : values()) {
                if (t.patterns != null) {
                    for (String m : t.patterns) {
                        if (pattern.equals(m)) {
                            return t;
                        }
                    }
                }
            }
        }
        return Unknown;
    }

    public static int getSize(long value) {
        int size = 0;
        for (int i = 0; i < 8; i++) {
            if ((value & 0xFF) != 0) {
                size = i + 1;
            }
            value >>>= 8;
        }
        //align
        switch (size) {
            case 1:
                size = 2;
                break;
            case 3:
                size = 4;
                break;
            case 5, 6, 7:
                size = 8;
                break;
        }
        return size;
    }

    public static Type getUnsignedBySize(int size) {
        for (Type t : values()) {
            if ((t.size == size) && (t.getMin() >= 0)) {
                return t;
            }
        }
        return Unknown;
    }

    public static Type getSignedBySize(int size) {
        for (Type t : values()) {
            if ((t.size == size) && (t.getMin() < 0)) {
                return t;
            }
        }
        return Unknown;
    }

    public static boolean isSigned(@NonNull Type type) {
        return switch (type) {
            case Int8, Int16, Int32 -> true;
            case UInt8, UInt16, UInt32 -> false;
            case Unknown -> throw new ArithmeticException();
        };
    }

    @Getter
    private int size;

    @Getter
    private long min;

    @Getter
    private long max;

    @Getter
    private String[] patterns;
}
