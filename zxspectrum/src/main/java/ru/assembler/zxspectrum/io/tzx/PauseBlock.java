package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PauseBlock extends Block {
    public static final int DEFAULT_ID = 0x20;

    @Getter
    protected int pause;

    public PauseBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        this.pause = IOUtils.readWord(is, ByteOrder.LittleEndian);
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        IOUtils.writeWord(os, (short) pause, ByteOrder.LittleEndian);
    }
}
