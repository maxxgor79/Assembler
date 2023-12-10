package ru.zxspectrum.io.tap;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@Slf4j
public class HeaderData implements TapElementReader, TapElementWriter {
    @Getter
    private int size = 2;

    @Getter
    private int type = 255;

    @Getter
    @NonNull
    private byte[] content;

    @Getter
    private int checkSum;

    public void setContent(@NonNull byte[] content) {
        if (this.content == content) {
            return;
        }
        this.content = content;
        size = 2 + content.length;
        checkSum = calcCheckSum();
    }

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        size = dis.readUnsignedShort();
        type = dis.readByte();
        content = new byte[size - 2];
        dis.read(content);
        checkSum = dis.readByte();
    }

    @Override
    public void write(@NonNull LEDataOutputStream dos) throws IOException {
        writeTap(dos);
    }

    @Override
    public void writeTap(@NonNull LEDataOutputStream dos) throws IOException {
        size = 2;
        if (content != null) {
            size += content.length;
        }
        dos.writeShort(size);
        dos.writeByte(type);
        if (content != null) {
            dos.write(content);
        }
        dos.writeByte(checkSum);
    }

    protected int calcCheckSum() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final LEDataOutputStream dos = new LEDataOutputStream(baos);
        try {
            dos.writeByte(type);
            if (content != null) {
                dos.write(content);
            }
            return TapUtil.calculateChecksum(baos.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }
}
