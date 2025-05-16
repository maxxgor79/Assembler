package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ToString
@Slf4j
public class PureToneBlock extends Block {
    public static final int DEFAULT_ID = 0x12;

    @Getter
    protected int tStateLength;

    @Getter
    protected int pulseNumber;

    private byte [] data;

    public PureToneBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        tStateLength = IOUtils.readWord(is, ByteOrder.LittleEndian);
        pulseNumber = IOUtils.readWord(is, ByteOrder.LittleEndian);
        data = generateTone(tStateLength, pulseNumber);
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        IOUtils.writeWord(os, (short)tStateLength, ByteOrder.LittleEndian);
        IOUtils.writeWord(os, (short)pulseNumber, ByteOrder.LittleEndian);
    }
    //TODO implementation
    private byte [] generateTone(int tStateLength, int pulseNumber) {
        return new byte[0];
    }
}
