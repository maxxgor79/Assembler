package ru.zxspectrum.io.tzx;

import lombok.NonNull;
import ru.assembler.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StopTape48kBlock extends Block {
    public static final int DEFAULT_ID = 0x2A;

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
    }
}
