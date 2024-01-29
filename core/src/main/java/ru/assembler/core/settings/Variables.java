package ru.assembler.core.settings;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */

public final class Variables {
    private Variables() {

    }

    public static final String LOG_FILE = "log_file";

    public static final String DEFAULT_ADDRESS = "default_address";

    public static final String MIN_ADDRESS = "min_address";

    public static final String MAX_ADDRESS = "max_address";

    public static final String BYTE_ORDER = "byte_order";

    public static final String SOURCE_ENCODING = "source_encoding";

    public static final String PLATFORM_ENCODING = "platform_encoding";

    public static final String MAJOR_VERSION = "major_version";

    public static final String MINOR_VERSION = "minor_version";

    public static final String OUTPUT_DIRECTORY = "output_directory";

    public static final String CMD_FILENAME = "cmd_filename";

    public static final String CPU_MODELS = "cpu_models";

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
            value = value.substring(2);
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
            value = value.substring(2);
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

    public static String getString(String name) {
        return System.getProperty(name);
    }

    public static void setString(@NonNull String name, @NonNull String value) {
        System.setProperty(name, value);
    }

    public static void load(@NonNull InputStream is) throws IOException {
        System.getProperties().load(is);
    }
}
