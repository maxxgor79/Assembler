package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBlock extends ReaderWriter {
    public static final int DEFAULT_BLOCK_ID = 0x10;

    @Getter
    @Setter
    protected int blockId = DEFAULT_BLOCK_ID;

    @Getter
    @Setter
    protected int pause;

    @Getter
    @Setter
    @NonNull
    protected byte[] data;

    @Override
    public void read(@NonNull final InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_BLOCK_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        this.pause = IOUtils.readWord(is, ByteOrder.LittleEndian);
        if (pause < 0) {
            throw new IOException("Bad pause value: " + pause);
        }
        final int size = IOUtils.readWord(is, ByteOrder.LittleEndian);
        if (size < 0) {
            throw new IOException("Bad size value: " + size);
        }
        this.data = new byte[size];
        is.read(data);
    }

    @Override
    public void write(@NonNull final OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        IOUtils.writeWord(os, (short) pause, ByteOrder.LittleEndian);
        IOUtils.writeWord(os, (short) data.length, ByteOrder.LittleEndian);
        os.write(data);
    }
}
