package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.bytecode.ByteOrder;
import ru.assembler.core.util.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ToString
@Slf4j
public class PureDataBlock extends Block {
    public static final int DEFAULT_ID = 0x14;

    @Getter
    protected int zeroLength;

    @Getter
    protected int oneLength;

    @Getter
    protected int bitsInLastByte;

    @Getter
    protected int pause;

    @Getter
    protected byte []data;

    public PureDataBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        zeroLength = IOUtils.readWord(is, ByteOrder.LittleEndian);
        oneLength = IOUtils.readWord(is, ByteOrder.LittleEndian);
        bitsInLastByte = IOUtils.readByte(is);
        pause = IOUtils.readWord(is, ByteOrder.LittleEndian);
        int len = 0;
        len |= (IOUtils.readByte(is) & 0xFF);
        len |= (IOUtils.readByte(is) & 0xFF) << 8;
        len |= (IOUtils.readByte(is) & 0xFF) << 16;
        data = new byte[len];
        System.out.println("len="+len);
        int readBytes = is.read(data);
        System.out.println("readBytes="+readBytes);
        if (readBytes != len) {
            throw new EOFException();
        }
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        IOUtils.writeByte(os, (byte) blockId);
        IOUtils.writeWord(os, (short) zeroLength, ByteOrder.LittleEndian);
        IOUtils.writeWord(os, (short) oneLength, ByteOrder.LittleEndian);
        IOUtils.writeByte(os, (byte) bitsInLastByte);
        IOUtils.writeWord(os, (short) pause, ByteOrder.LittleEndian);
        //TODO data saving
    }

    @Override
    public void export(@NonNull OutputStream os) throws IOException {
        os.write(getData());
    }
}
