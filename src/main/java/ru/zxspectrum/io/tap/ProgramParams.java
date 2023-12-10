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

@ToString
@Slf4j
public class ProgramParams implements TapElementReader, TapElementWriter, Binary {
    @Setter
    @Getter
    private int autostartLine;//2b

    @Setter
    @Getter
    private int programSize;//2b

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        autostartLine = dis.readUnsignedShort();
        programSize = dis.readUnsignedShort();
    }

    @Override
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        writeTap(dos);
    }

    @Override
    public void writeTap(LEDataOutputStream dos) throws IOException {
        dos.writeShort(autostartLine);
        dos.writeShort(programSize);
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            LEDataOutputStream dos = new LEDataOutputStream(baos);
            dos.writeShort(autostartLine);
            dos.writeShort(programSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
        return baos.toByteArray();
    }
}
