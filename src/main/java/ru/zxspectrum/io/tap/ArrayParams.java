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
import java.io.InvalidObjectException;
import java.util.Arrays;

@ToString
@Slf4j
public class ArrayParams implements TapElementReader, TapElementWriter, Binary {
    @Setter
    @Getter
    @NonNull
    private int reserved1;//1b

    @Setter
    @Getter
    @NonNull
    private int varName;//1b

    @Setter
    @Getter
    @NonNull
    private byte[] reserved2 = {0, -128};

    @Override
    public void read(@NonNull final LEDataInputStream dis) throws IOException {
        reserved1 = dis.readUnsignedByte();
        varName = dis.readUnsignedByte();
        dis.readFully(reserved2, 0, 2);
        if (!Arrays.equals(reserved2, new byte[]{0, -128})) {
            throw new InvalidObjectException("Bad validation: " + Arrays.toString(reserved2));
        }
    }

    @Override
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        dos.writeByte(reserved1);
        dos.writeByte(varName);
        dos.write(reserved2);
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            LEDataOutputStream dos = new LEDataOutputStream(baos);
            dos.writeByte(reserved1);
            dos.writeByte(varName);
            dos.write(reserved2);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }
}
