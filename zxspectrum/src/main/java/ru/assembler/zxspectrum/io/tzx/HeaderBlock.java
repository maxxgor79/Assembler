package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.util.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

@Slf4j
@ToString
public class HeaderBlock extends Block {

    public static final String ID = "ZXTape!";

    public static final int BLOCK_ID = 0x1a;

    public static final int DEFAULT_MAJOR_VERSION = 1;

    public static final int DEFAULT_MINOR_VERSION = 1;

    @Getter
    @Setter
    @NonNull
    protected String id = ID;

    @Getter
    @Setter
    protected int blockId = BLOCK_ID;

    @Getter
    @Setter
    protected int majorVersion = DEFAULT_MAJOR_VERSION;

    @Getter
    @Setter
    protected int minorVersion = DEFAULT_MINOR_VERSION;

    @Override
    public void read(@NonNull final InputStream is) throws IOException {
        final byte[] id = new byte[ID.length()];
        int readBytes = is.read(id);
        if (readBytes != id.length) {
            throw new EOFException();
        }
        this.id = new String(id);
        if (!Arrays.equals(id, ID.getBytes())) {
            throw new IOException("Bad ID: " + this.id);
        }
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != 0x1a) {
            throw new IOException("Bad block id: " + this.blockId);
        }
        this.majorVersion = IOUtils.readByte(is);
        this.minorVersion = IOUtils.readByte(is);
        if (majorVersion > DEFAULT_MAJOR_VERSION) {
            throw new IOException("Bad major version");
        }
    }

    @Override
    public void write(@NonNull final OutputStream os) throws IOException {
        os.write(this.id.getBytes());
        os.write(blockId);
        os.write(majorVersion);
        os.write(minorVersion);
    }
}
