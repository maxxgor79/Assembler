package ru.zxspectrum.io.tap;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@ToString
public class BytesParams implements TapElementReader, TapElementWriter, Binary {
    @Setter
    @Getter
    private int startAddress;//2b

    @Setter
    @Getter
    private int reserved = 32768;//2b

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        startAddress = dis.readUnsignedShort();
        reserved = dis.readUnsignedShort();
    }

    @Override
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        writeTap(dos);
    }

    @Override
    public void writeTap(LEDataOutputStream dos) throws IOException {
        dos.writeShort(startAddress);
        dos.writeShort(reserved);
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            LEDataOutputStream dos = new LEDataOutputStream(baos);
            dos.writeShort(startAddress);
            dos.writeShort(reserved);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }
}
