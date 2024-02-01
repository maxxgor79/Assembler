package ru.assembler.core.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.bytecode.ByteOrder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */

@Slf4j
public final class IOUtils {
    private IOUtils() {

    }

    public static void writeString(@NonNull OutputStream os, @NonNull String value) throws IOException {
        try {
            os.write(value.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static void writeByte(@NonNull OutputStream os, byte value) throws IOException {
        try {
            os.write(value);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static void writeWord(@NonNull OutputStream os, short value, @NonNull ByteOrder order)
            throws IOException {
        try {
            switch (order) {
                case LittleEndian -> {
                    os.write((byte) value);
                    os.write((byte) (value >>> 8));
                }
                case BigEndian -> {
                    os.write((byte) (value >>> 8));
                    os.write((byte) value);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static int readWord(@NonNull InputStream is, @NonNull ByteOrder order) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return switch (order) {
            case LittleEndian -> (ch2 << 8) + (ch1 << 0);
            case BigEndian -> (ch2 << 0) + (ch1 << 8);
        };
    }

    public static int readByte(@NonNull InputStream is) throws IOException {
        int ch = is.read();
        if (ch == -1) {
            throw new EOFException();
        }
        return ch;
    }
}
