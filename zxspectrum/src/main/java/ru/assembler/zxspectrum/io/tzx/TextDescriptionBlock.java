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

@Slf4j
@ToString
public class TextDescriptionBlock extends Block {
    public static final int DEFAULT_ID = 0x30;

    @Getter
    @Setter
    protected String message;

    public TextDescriptionBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        final int len = IOUtils.readByte(is);
        final byte[] data = new byte[len];
        final int readBytes = is.read(data);
        if (readBytes != len) {
            throw new EOFException();
        }
        this.message = new String(data);
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        int len = message == null ? 0 : message.length();
        IOUtils.writeByte(os, (byte) len);
        if (len != 0) {
            os.write(message.getBytes());
        }
    }
}
