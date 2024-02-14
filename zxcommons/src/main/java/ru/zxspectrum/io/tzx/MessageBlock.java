package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MessageBlock extends Block {
    public static final int DEFAULT_ID = 0x31;

    @Getter
    protected String text;

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        final int len = IOUtils.readWord(is, ByteOrder.LittleEndian);
        final byte[] data = new byte[len];
        final int readBytes = is.read(data);
        if (readBytes != len) {
            throw new EOFException();
        }
        this.text = new String(data);
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        int len = text == null ? 0 : text.length();
        IOUtils.writeByte(os, (byte) len);
        if (len != 0) {
            os.write(text.getBytes());
        }
    }
}
