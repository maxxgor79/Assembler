package ru.zxspectrum.io.tap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;

@ToString
@EqualsAndHashCode
public class HeaderlessData implements TapElementReader, TapElementWriter {
    private final int length;

    @Getter
    private byte[] content;

    @Getter
    private int checkSum;

    public HeaderlessData(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    public void setContent(@NonNull byte[] content) {
        this.content = content;
        checkSum = calcCheckSum();
    }

    protected int calcCheckSum() {
        int checkSum = Flag.Data.getCode();
        if (content != null) {
            return checkSum ^ TapUtil.calculateChecksum(content);
        }
        return checkSum;
    }

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        content = new byte[length - 2];
        dis.read(content);
        checkSum = dis.readByte();
    }

    @Override
    public void write(@NonNull LEDataOutputStream dos) throws IOException {
        writeTap(dos);
    }

    @Override
    public void writeTap(@NonNull LEDataOutputStream dos) throws IOException {
        if (content != null) {
            dos.write(content);
        }
        dos.writeByte(checkSum);
    }
}
