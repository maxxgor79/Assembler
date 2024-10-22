package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ToString
public class ArchiveInfoBlock extends Block {
    public static final int DEFAULT_ID = 0x32;

    @Getter
    private String text;

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        int len = IOUtils.readWord(is, ByteOrder.LittleEndian);
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        byte []data = new byte[len];
        is.read(data);
        text = new String(data);
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
