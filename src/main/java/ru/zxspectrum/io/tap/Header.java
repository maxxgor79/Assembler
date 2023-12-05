package ru.zxspectrum.io.tap;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@ToString
public class Header implements TapElementReader, TapElementWriter, Binary {
    @Setter
    @Getter
    @NonNull
    private HeaderType headerType;

    @NonNull
    private final byte[] filename = new byte[10];

    @Setter
    @Getter
    private int dataSize;

    @Setter
    @Getter
    @NonNull
    private ArrayParams numberParams;

    @Setter
    @Getter
    @NonNull
    private ArrayParams charParams;

    @Setter
    @Getter
    @NonNull
    private ProgramParams programParams;

    @Getter
    @Setter
    @NonNull
    private BytesParams bytesParams;

    @Getter
    private int checksum;//???


    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        int b = dis.readUnsignedByte();
        headerType = HeaderType.getByCode(b);
        if (headerType == null) {
            throw new IOException("Bad header type b=" + b);
        }
        IOUtils.read(dis, filename, 0, filename.length);
        dataSize = dis.readUnsignedShort();
        switch (headerType) {
            case Program -> {
                programParams = new ProgramParams();
                programParams.read(dis);
            }
            case CharArray -> {
                charParams = new ArrayParams();
                charParams.read(dis);
            }
            case Bytes -> {
                bytesParams = new BytesParams();
                bytesParams.read(dis);
            }
            case NumberArray -> {
                numberParams = new ArrayParams();
                numberParams.read(dis);
            }
        }
        checksum = dis.readUnsignedByte();
        int calculated = calculateChecksum(getBytes());
        if (checksum != calculated) {
            throw new IllegalStateException("Bad checksum, loaded=" + checksum + ", calculated=" + calculated);
        }
    }

    @Override
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        byte[] data = getBytes();
        dos.write(data);
        checksum = calculateChecksum(data);
        dos.writeByte(checksum);
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            LEDataOutputStream dos = new LEDataOutputStream(baos);
            if (headerType != null) {
                dos.writeByte(headerType.getCode());
            }
            dos.write(filename);
            dos.writeShort(dataSize);
            switch (headerType) {
                case Program -> {
                    if (programParams != null) {
                        dos.write(programParams.getBytes());
                    }
                }
                case CharArray -> {
                    if (charParams != null) {
                        dos.write(charParams.getBytes());
                    }
                }
                case Bytes -> {
                    if (bytesParams != null) {
                        dos.write(bytesParams.getBytes());
                    }
                }
                case NumberArray -> {
                    if (numberParams != null) {
                        dos.write(numberParams.getBytes());
                    }
                }
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    protected static int calculateChecksum(@NonNull byte[] data) {
        if (data.length == 0) {
            return 0;
        }
        int value = data[0];
        for (int i = 1; i < data.length; i++) {
            value ^= data[i];
        }
        return value & 0xFF;
    }

    public String getFilename() {
        return new String(filename);
    }

    public void setFilename(@NonNull final String filename) {
        byte[] src = filename.getBytes();
        System.arraycopy(src, 0, this.filename, 0, src.length > 10 ? 10 : src.length);
    }
}
