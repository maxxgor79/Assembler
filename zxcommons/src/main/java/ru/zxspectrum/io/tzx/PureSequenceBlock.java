package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.util.IOUtils;
import ru.assembler.core.compiler.bytecode.ByteOrder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ToString
@Slf4j
public class PureSequenceBlock extends Block {
    public static final int DEFAULT_ID = 0x13;

    @Getter
    private int[] pulsesLengths;

    public PureSequenceBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        int n = IOUtils.readByte(is);
        pulsesLengths = new int[n];
        for (int i = 0; i < n; i++) {
            pulsesLengths[i] = IOUtils.readWord(is, ByteOrder.LittleEndian);
        }
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        if (pulsesLengths != null) {
            IOUtils.writeByte(os, (byte) pulsesLengths.length);
            for (int i = 0; i < pulsesLengths.length; i++) {
                IOUtils.writeWord(os, (short) pulsesLengths[i], ByteOrder.LittleEndian);
            }
        } else {
            IOUtils.writeByte(os, (byte) 0);
        }
    }
}
