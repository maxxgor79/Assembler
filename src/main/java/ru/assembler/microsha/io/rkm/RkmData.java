package ru.assembler.microsha.io.rkm;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class RkmData {
    @Setter
    @Getter
    private int startAddress16;

    @Getter
    @NonNull
    private byte[] data;

    @Getter
    private int crc16;

    public RkmData(final int startAddress16, final byte[] data) {
        this.startAddress16 = startAddress16;
        setData(data);
    }

    public void save(@NonNull OutputStream os) throws IOException {
        Objects.requireNonNull(data);
        IOUtils.writeWord(os, (short) startAddress16, ByteOrder.BigEndian);
        IOUtils.writeWord(os, (short) (startAddress16 + data.length), ByteOrder.BigEndian);
        os.write(data);
        IOUtils.writeWord(os, (short) calcCrc16(data), ByteOrder.BigEndian);
    }

    public void read(@NonNull InputStream is) throws IOException {
        startAddress16 = IOUtils.readWord(is, ByteOrder.BigEndian);
        final int endAddress16 = IOUtils.readWord(is, ByteOrder.BigEndian);
        final int size16 = endAddress16 - startAddress16 + 1;
        data = new byte[size16];
        int readBytes = is.read(data);
        if (readBytes != size16) {
            throw new EOFException();
        }
        crc16 = IOUtils.readWord(is, ByteOrder.BigEndian);
        if (calcCrc16(data) != crc16) {
            throw new IOException("Bad crc16");
        }
    }

    public void setData(@NonNull final byte[] data) {
        this.data = data;
        crc16 = calcCrc16(this.data);
    }

    public static int calcCrc16(@NonNull final byte[] data) {
        int csm_lo = 0, csm_hi = 0;
        for (int i = 0; i < data.length; i++) {
            if (i++ % 2 == 0) {
                csm_lo ^= data[i] & 0x00FF;
            } else {
                csm_hi ^= data[i] & 0x00FF;
            }
        }
        return csm_hi << 8 | csm_lo;
    }

    public RkmData copy() {
        RkmData instance = new RkmData(startAddress16, data);
        return instance;
    }
}
