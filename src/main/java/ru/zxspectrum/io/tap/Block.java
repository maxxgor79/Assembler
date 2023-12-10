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
    private byte[] bytes;

    @Setter
    @Getter
    @NonNull
    private byte[] headerlessBytes;

    public byte[] getContent() {
        return bytes == null ? headerlessBytes : bytes;
    }

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        blockLength = dis.readUnsignedShort();
        int b = dis.readUnsignedByte();
        flag = Flag.findByCode(b);
        if (flag == null) {
            throw new IOException("Bad flag format b=" + b);
        }
        if (blockLength == 19) {
            if (flag == Flag.Header) {
                header = new Header();
                header.read(dis);
            }
            bytes = IOUtils.readFully(dis, header.getDataSize() + 4); //line number(2b) + basic data size(2b)
        }
        if (flag == Flag.Data || blockLength != 19) {
            headerlessBytes = IOUtils.readFully(dis, blockLength - 1);// - sizeof(code) 1b
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
        if (blockLength == 19 && flag == Flag.Header && header != null) {
            header.write(dos);
        }
        if (blockLength == 19 && bytes != null) {
            dos.write(bytes);
        }
        if (flag == Flag.Data && headerlessBytes != null) {
            dos.write(headerlessBytes);
        }
    }
}
