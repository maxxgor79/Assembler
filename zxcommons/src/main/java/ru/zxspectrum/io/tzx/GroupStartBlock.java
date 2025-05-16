package ru.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.util.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

@ToString
@Slf4j
public class GroupStartBlock extends Block {
    public static final int DEFAULT_ID = 0x21;

    @Getter
    protected String name = null;

    @Getter
    protected final List<Block> blocks = new ArrayList<>();

    public GroupStartBlock() {
        blockId = DEFAULT_ID;
    }

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        this.blockId = IOUtils.readByte(is);
        if (this.blockId != DEFAULT_ID) {
            throw new IOException("Bad block id: " + blockId);
        }
        int length = IOUtils.readByte(is);
        if (length < 0) {
            throw new IllegalArgumentException("Bad name length: " + length);
        }

        if (length > 0) {
            byte[] data = new byte[length];
            if (is.read(data) != length) {
                throw new EOFException();
            }
            name = new String(data);
        }
        final PushbackInputStream pis = new PushbackInputStream(is);
        do {
            final int b = pis.read();
            if (b == -1) {
                break;
            }
            Block block = null;
            log.info("block id=" + b);
            if (BlockFactory.isBlockSupported(b)) {
                pis.unread(b);
                block = BlockFactory.construct(b);
            }
            switch (b) {
                case 0x22: //END OF GROUP
                    return;
            }
            if (block != null) {
                block.read(pis);
                blocks.add(block);
            } else {
                throw new IOException("Unknown byte: " + b);
            }
        } while (true);
    }

    @Override
    public void write(@NonNull OutputStream os) throws IOException {
        for (Block block : blocks) {
            block.write(os);
        }
    }

    @Override
    public void export(@NonNull OutputStream os) throws IOException {
        for (Block block : blocks) {
            block.export(os);
        }
    }
}