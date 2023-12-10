package ru.zxspectrum.io.tap;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;

@Slf4j
@ToString
public class Block implements TapElementReader, TapElementWriter {

    public static final int DEFAULT_BLOCK_LENGTH = 19;

    @Setter
    @Getter
    @NonNull
    private int blockLength;

    @Setter
    @Getter
    @NonNull
    private Flag flag;

    @Setter
    @Getter
    @NonNull
    private Header header;

    @Setter
    @Getter
    @NonNull
    private HeaderData headerData;

    @Setter
    @Getter
    @NonNull
    private HeaderlessData headerlessData;

    public byte[] getContent() {
        return headerData == null ? headerlessData.getContent() : headerData.getContent();
    }

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        blockLength = dis.readUnsignedShort();
        int b = dis.readUnsignedByte();
        flag = Flag.findByCode(b);
        if (flag == null) {
            throw new IOException("Bad flag format b=" + b);
        }
        if (blockLength == DEFAULT_BLOCK_LENGTH) {
            if (flag == Flag.Header) {
                header = new Header();
                header.read(dis);
            }
            headerData = new HeaderData();
            headerData.read(dis);//data size(2b) + Type(1b) + checkSum(1b)
        }
        if (flag == Flag.Data || blockLength != DEFAULT_BLOCK_LENGTH) {
            headerlessData = new HeaderlessData(blockLength);
            headerlessData.read(dis);
        }
    }

    @Override
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        dos.writeShort(blockLength);
        writeTap(dos);
    }

    @Override
    public void writeTap(LEDataOutputStream dos) throws IOException {
        if (flag != null) {
            dos.writeByte(flag.getCode());
        }
        if (blockLength == DEFAULT_BLOCK_LENGTH && flag == Flag.Header && header != null) {
            header.write(dos);
        }
        if (blockLength == DEFAULT_BLOCK_LENGTH && headerData != null) {
            headerData.writeTap(dos);
        }
        if (flag == Flag.Data && headerlessData != null) {
            headerlessData.writeTap(dos);
        }
    }
}
