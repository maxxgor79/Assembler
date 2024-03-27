package ru.zxspectrum.disassembler.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.io.*;

/**
 * @author maxim
 * Date: 12/24/2023
 */
@Slf4j
public class ByteCodeInputStream extends InputStream {
    @Getter
    private final ByteOrder byteOrder;

    @Getter
    private int pc;

    private int prevPc;

    @Getter
    private File file;

    @Getter
    private byte[] data;

    public ByteCodeInputStream(final byte[] data) {
        this(data, ByteOrder.LittleEndian);
    }

    public ByteCodeInputStream(@NonNull final File file) throws IOException {
        this(file, ByteOrder.LittleEndian);
    }

    public ByteCodeInputStream(@NonNull final File file, @NonNull final ByteOrder byteOrder) throws IOException {
        this.file = file;
        this.byteOrder = byteOrder;
        try (FileInputStream fis = new FileInputStream(file)) {
            this.data = IOUtils.toByteArray(fis);
        }
    }

    public ByteCodeInputStream(@NonNull final byte[] data, @NonNull final ByteOrder byteOrder) {
        this.data = data;
        this.byteOrder = byteOrder;
    }

    @Override
    public int read() throws IOException {
        if (pc >= data.length) {
            return -1;
        }
        return data[prevPc = pc++] & 0xFF;
    }

    public int readByte() throws IOException {
        return (byte) read();
    }

    public int readShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        switch (byteOrder) {
            case LittleEndian -> {
                return (ch1 << 0) + (ch2 << 8);
            }
            case BigEndian -> {
                return (ch1 << 8) + (ch2 << 0);
            }
        }
        return -1;
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 0xFFFF;
    }

    public int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        switch (byteOrder) {
            case LittleEndian -> {
                return (ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24);
            }
            case BigEndian -> {
                return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
            }
        }
        return -1;
    }

    public long readUnsignedInt() throws IOException {
        return readInt() & 0xFFFF_FFFF;
    }

    public int jump(int pos) {
        if (pos < 0) {
            pos = 0;
        }
        if (pos >= data.length) {
            pos = data.length;
        }
        prevPc = pc;
        pc = pos;
        return pc;
    }

    public int moveTo(int offset) {
        int newPos = pc + offset;
        return jump(newPos);
    }

    public void pushback() {
        pc = prevPc;
    }

    @Override
    public ByteCodeInputStream clone() {
        return new ByteCodeInputStream(data, byteOrder);
    }

    public int size() {
        return data.length;
    }
}
