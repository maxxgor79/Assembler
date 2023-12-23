package ru.zxspectrum.assembler.lang;

import lombok.NonNull;
import ru.zxspectrum.assembler.error.ConversationException;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Maxim Gorin
 */
public final class TypeConverter {
    private TypeConverter() {

    }

    static BigInteger convertInt8ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertInt8ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertInt8ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertInt8ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xff);
    }

    static BigInteger convertInt8ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertInt8ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt8ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertUInt8ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt8ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt8ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt8ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt8ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertInt16ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertInt16ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertInt16ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertInt16ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertInt16ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertInt16ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertUInt16ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertUInt16ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt16ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertUInt16ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertUInt16ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertUInt16ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertInt32ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertInt32ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertInt32ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertInt32ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertInt32ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.intValue());
    }

    static BigInteger convertInt32ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.intValue() & 0xffffffff);
    }

    static BigInteger convertUInt32ToInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue());
    }

    static BigInteger convertUInt32ToUInt8(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.byteValue() & 0xff);
    }

    static BigInteger convertUInt32ToInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue());
    }

    static BigInteger convertUInt32ToUInt16(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.shortValue() & 0xffff);
    }

    static BigInteger convertUInt32ToInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.intValue());
    }

    static BigInteger convertUInt32ToUInt32(@NonNull BigInteger val) {
        return BigInteger.valueOf(val.intValue() & 0xffffffff);
    }

    static BigInteger wrongConvert(@NonNull BigInteger val) {
        throw new ConversationException();
    }

    private static Map<Type, Map<Type, Converter>> nonStrictConverterTable = new EnumMap<>(Type.class);

    static {
        final Map<Type, Converter> int8ConverterTable = new EnumMap<>(Type.class);
        int8ConverterTable.put(Type.Int8, TypeConverter::convertInt8ToInt8);
        int8ConverterTable.put(Type.UInt8, TypeConverter::convertInt8ToUInt8);
        int8ConverterTable.put(Type.Int16, TypeConverter::convertInt8ToInt16);
        int8ConverterTable.put(Type.UInt16, TypeConverter::convertInt8ToUInt16);
        int8ConverterTable.put(Type.Int32, TypeConverter::convertInt8ToInt32);
        int8ConverterTable.put(Type.UInt32, TypeConverter::convertInt8ToUInt32);
        nonStrictConverterTable.put(Type.Int8, int8ConverterTable);
        final Map<Type, Converter> uInt8ConverterTable = new EnumMap<>(Type.class);
        uInt8ConverterTable.put(Type.Int8, TypeConverter::convertUInt8ToInt8);
        uInt8ConverterTable.put(Type.UInt8, TypeConverter::convertUInt8ToUInt8);
        uInt8ConverterTable.put(Type.Int16, TypeConverter::convertUInt8ToInt16);
        uInt8ConverterTable.put(Type.UInt16, TypeConverter::convertUInt8ToUInt16);
        uInt8ConverterTable.put(Type.Int32, TypeConverter::convertUInt8ToInt32);
        uInt8ConverterTable.put(Type.UInt32, TypeConverter::convertUInt8ToUInt32);
        nonStrictConverterTable.put(Type.UInt8, uInt8ConverterTable);
        final Map<Type, Converter> int16ConverterTable = new EnumMap<>(Type.class);
        int16ConverterTable.put(Type.Int8, TypeConverter::convertInt16ToInt8);
        int16ConverterTable.put(Type.UInt8, TypeConverter::convertInt16ToUInt8);
        int16ConverterTable.put(Type.Int16, TypeConverter::convertInt16ToInt16);
        int16ConverterTable.put(Type.UInt16, TypeConverter::convertInt16ToUInt16);
        int16ConverterTable.put(Type.Int32, TypeConverter::convertInt16ToInt32);
        int16ConverterTable.put(Type.UInt32, TypeConverter::convertInt16ToUInt32);
        nonStrictConverterTable.put(Type.Int16, int16ConverterTable);
        final Map<Type, Converter> uInt16ConverterTable = new EnumMap<>(Type.class);
        uInt16ConverterTable.put(Type.Int8, TypeConverter::convertUInt16ToInt8);
        uInt16ConverterTable.put(Type.UInt8, TypeConverter::convertUInt16ToUInt8);
        uInt16ConverterTable.put(Type.Int16, TypeConverter::convertUInt16ToInt16);
        uInt16ConverterTable.put(Type.UInt16, TypeConverter::convertUInt16ToUInt16);
        uInt16ConverterTable.put(Type.Int32, TypeConverter::convertUInt16ToInt32);
        uInt16ConverterTable.put(Type.UInt32, TypeConverter::convertUInt16ToUInt32);
        nonStrictConverterTable.put(Type.UInt16, uInt16ConverterTable);
        final Map<Type, Converter> int32ConverterTable = new EnumMap<>(Type.class);
        int32ConverterTable.put(Type.Int8, TypeConverter::convertInt32ToInt8);
        int32ConverterTable.put(Type.UInt8, TypeConverter::convertInt32ToUInt8);
        int32ConverterTable.put(Type.Int16, TypeConverter::convertInt32ToInt16);
        int32ConverterTable.put(Type.UInt16, TypeConverter::convertInt32ToUInt16);
        int32ConverterTable.put(Type.Int32, TypeConverter::convertInt32ToInt32);
        int32ConverterTable.put(Type.UInt32, TypeConverter::convertInt32ToUInt32);
        nonStrictConverterTable.put(Type.Int32, int32ConverterTable);
        final Map<Type, Converter> uInt32ConverterTable = new EnumMap<>(Type.class);
        uInt32ConverterTable.put(Type.Int8, TypeConverter::convertUInt32ToInt8);
        uInt32ConverterTable.put(Type.UInt8, TypeConverter::convertUInt32ToUInt8);
        uInt32ConverterTable.put(Type.Int16, TypeConverter::convertUInt32ToInt16);
        uInt32ConverterTable.put(Type.UInt16, TypeConverter::convertUInt32ToUInt16);
        uInt32ConverterTable.put(Type.Int32, TypeConverter::convertUInt32ToInt32);
        uInt32ConverterTable.put(Type.UInt32, TypeConverter::convertUInt32ToUInt32);
        nonStrictConverterTable.put(Type.UInt32, uInt32ConverterTable);
    }

    public static BigInteger nonStrictConvert(@NonNull Type src, @NonNull BigInteger val, @NonNull Type dst) {
        Map<Type, Converter> funcMap = nonStrictConverterTable.get(src);
        return funcMap.get(dst).apply(val);
    }

    private static Map<Type, Map<Type, Converter>> strictConverterTable = new EnumMap<>(Type.class);

    static {
        final Map<Type, Converter> int8ConverterTable = new EnumMap<>(Type.class);
        int8ConverterTable.put(Type.Int8, TypeConverter::convertInt8ToInt8);
        int8ConverterTable.put(Type.UInt8, TypeConverter::convertInt8ToUInt8);
        int8ConverterTable.put(Type.Int16, TypeConverter::convertInt8ToInt16);
        int8ConverterTable.put(Type.UInt16, TypeConverter::convertInt8ToUInt16);
        int8ConverterTable.put(Type.Int32, TypeConverter::convertInt8ToInt32);
        int8ConverterTable.put(Type.UInt32, TypeConverter::convertInt8ToUInt32);
        strictConverterTable.put(Type.Int8, int8ConverterTable);
        final Map<Type, Converter> uInt8ConverterTable = new EnumMap<>(Type.class);
        uInt8ConverterTable.put(Type.Int8, TypeConverter::convertUInt8ToInt8);
        uInt8ConverterTable.put(Type.UInt8, TypeConverter::convertUInt8ToUInt8);
        uInt8ConverterTable.put(Type.Int16, TypeConverter::convertUInt8ToInt16);
        uInt8ConverterTable.put(Type.UInt16, TypeConverter::convertUInt8ToUInt16);
        uInt8ConverterTable.put(Type.Int32, TypeConverter::convertUInt8ToInt32);
        uInt8ConverterTable.put(Type.UInt32, TypeConverter::convertUInt8ToUInt32);
        strictConverterTable.put(Type.UInt8, uInt8ConverterTable);
        final Map<Type, Converter> int16ConverterTable = new EnumMap<>(Type.class);
        int16ConverterTable.put(Type.Int8, TypeConverter::wrongConvert);
        int16ConverterTable.put(Type.UInt8, TypeConverter::wrongConvert);
        int16ConverterTable.put(Type.Int16, TypeConverter::convertInt16ToInt16);
        int16ConverterTable.put(Type.UInt16, TypeConverter::convertInt16ToUInt16);
        int16ConverterTable.put(Type.Int32, TypeConverter::convertInt16ToInt32);
        int16ConverterTable.put(Type.UInt32, TypeConverter::convertInt16ToUInt32);
        strictConverterTable.put(Type.Int16, int16ConverterTable);
        final Map<Type, Converter> uInt16ConverterTable = new EnumMap<>(Type.class);
        uInt16ConverterTable.put(Type.Int8, TypeConverter::wrongConvert);
        uInt16ConverterTable.put(Type.UInt8, TypeConverter::wrongConvert);
        uInt16ConverterTable.put(Type.Int16, TypeConverter::convertUInt16ToInt16);
        uInt16ConverterTable.put(Type.UInt16, TypeConverter::convertUInt16ToUInt16);
        uInt16ConverterTable.put(Type.Int32, TypeConverter::convertUInt16ToInt32);
        uInt16ConverterTable.put(Type.UInt32, TypeConverter::convertUInt16ToUInt32);
        strictConverterTable.put(Type.UInt16, uInt16ConverterTable);
        final Map<Type, Converter> int32ConverterTable = new EnumMap<>(Type.class);
        int32ConverterTable.put(Type.Int8, TypeConverter::wrongConvert);
        int32ConverterTable.put(Type.UInt8, TypeConverter::wrongConvert);
        int32ConverterTable.put(Type.Int16, TypeConverter::wrongConvert);
        int32ConverterTable.put(Type.UInt16, TypeConverter::wrongConvert);
        int32ConverterTable.put(Type.Int32, TypeConverter::convertInt32ToInt32);
        int32ConverterTable.put(Type.UInt32, TypeConverter::convertInt32ToUInt32);
        strictConverterTable.put(Type.Int32, int32ConverterTable);
        final Map<Type, Converter> uInt32ConverterTable = new EnumMap<>(Type.class);
        uInt32ConverterTable.put(Type.Int8, TypeConverter::wrongConvert);
        uInt32ConverterTable.put(Type.UInt8, TypeConverter::wrongConvert);
        uInt32ConverterTable.put(Type.Int16, TypeConverter::wrongConvert);
        uInt32ConverterTable.put(Type.UInt16, TypeConverter::wrongConvert);
        uInt32ConverterTable.put(Type.Int32, TypeConverter::convertUInt32ToInt32);
        uInt32ConverterTable.put(Type.UInt32, TypeConverter::convertUInt32ToUInt32);
        strictConverterTable.put(Type.UInt32, uInt32ConverterTable);
    }

    public static BigInteger strictConvert(@NonNull Type src, @NonNull BigInteger val, @NonNull Type dst) {
        Map<Type, Converter> funcMap = strictConverterTable.get(src);
        return funcMap.get(dst).apply(val);
    }

    public static BigInteger convert(Type src, BigInteger val, Type dst, boolean strict) {
        if (strict) {
            return strictConvert(src, val, dst);
        }
        return nonStrictConvert(src, val, dst);
    }
}
