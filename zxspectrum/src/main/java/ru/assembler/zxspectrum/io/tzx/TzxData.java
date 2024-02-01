package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;

public class TzxData extends ReaderWriter {
    @Getter
    final private HeaderBlock headerBlock = new HeaderBlock();

    @Getter
    final private List<DataBlock> blocks = new LinkedList<>();

    @Override
    public void read(@NonNull final InputStream is) throws IOException {
        final PushbackInputStream pis = new PushbackInputStream(is);
        headerBlock.read(pis);
        blocks.clear();
        try {
            while (true) {
                final int b = pis.read();
                if (b == DataBlock.DEFAULT_BLOCK_ID) {
                    pis.unread(b);
                    DataBlock block = new DataBlock();
                    block.read(pis);
                    blocks.add(block);
                } else  {
                    break;
                }
            }
        } catch (EOFException e) {
        }
    }

    @Override
    public void write(@NonNull final OutputStream os) throws IOException {
        headerBlock.write(os);
        for (DataBlock block : blocks) {
            block.write(os);
        }
    }

    public void add(@NonNull final DataBlock block) {
        this.blocks.add(block);
    }
}
