package ru.zxspectrum.assembler.settings;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public final class Variables {
    private Variables() {

    }

    public static final String DEFAULT_ADDRESS = "default_address";

    public static final String MIN_ADDRESS = "min_address";

    public static final String MAX_ADDRESS = "max_address";

    public static final String BYTE_ORDER = "byte_order";

    public static final String SOURCE_ENCODING = "source_encoding";

    public static final String PLATFORM_ENCODING = "platform_encoding";

    public static int getInt(String name, int defaultValue) {
        if (name == null) {
            return defaultValue;
        }
        String value = System.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        int radix = 10;
        if (value.toLowerCase().endsWith("h")) {
            value = value.substring(0, value.length() - 1);
            radix = 16;
        }
        if (value.toLowerCase().startsWith("0x")) {
            value = value.substring(2, value.length());
            radix = 16;
        }
        if (value.toLowerCase().endsWith("b")) {
            value = value.substring(0, value.length() - 1);
            radix = 2;
        }
        try {
            return Integer.parseInt(value, radix);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static BigInteger getBigInteger(String name, BigInteger def) {
        if (name == null) {
            return def;
        }
        String value = System.getProperty(name);
        if (value == null) {
            return def;
        }
        int radix = 10;
        if (value.toLowerCase().endsWith("h")) {
            value = value.substring(0, value.length() - 1);
            radix = 16;
        }
        if (value.toLowerCase().startsWith("0x")) {
            value = value.substring(2, value.length());
            radix = 16;
        }
        if (value.toLowerCase().endsWith("b")) {
            value = value.substring(0, value.length() - 1);
            radix = 2;
        }
        try {
            return new BigInteger(value, radix);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getString(String name, String def) {
        return System.getProperty(name, def);
    }

    public static void load(InputStream is) throws IOException {
        System.getProperties().load(is);
    }

}
