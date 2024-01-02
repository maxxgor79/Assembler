package ru.zxspectrum.assembler.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.compiler.bytecode.ByteOrder;

import java.io.IOException;
import java.io.OutputStream;

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
}
