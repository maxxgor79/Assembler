package ru.zxspectrum.assembler.lang;

import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public enum Type {
    Int8(1, -128, 127), UInt8(1, 0, 255), Int16(2, -32768, 32767)
    , UInt16(2, 0, 65535), Int32(4, Integer.MIN_VALUE, Integer.MAX_VALUE)
    , UInt32(4, 0, 0xFFFFFFFF), Unknown(0, 0, 0);

    Type(int size, long min, long max) {
        this.size = size;
        this.min = min;
        this.max = max;
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

    @Getter
    private int size;

    @Getter
    private long min;

    @Getter
    private long max;
}
