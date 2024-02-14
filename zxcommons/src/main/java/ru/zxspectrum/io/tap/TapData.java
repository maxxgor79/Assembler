package ru.zxspectrum.io.tap;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.LEDataInputStream;

/**
 * @author Maxim Gorin
 */
@Slf4j
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TapData implements TapElementReader, TapElementWriter {
    private final List<Block> blockList = new ArrayList<>();

    @Override
    public void read(@NonNull InputStream is) throws IOException {
        LEDataInputStream dis = new LEDataInputStream(is);
        try {
            Block block;
            while (true) {
                int blockLength = dis.readUnsignedShort();
                int code = dis.read();
                if (code == HeaderBlock.TYPE && blockLength == HeaderBlock.SIZE) {
                    block = new HeaderBlock();
                } else if (code == DataBlock.TYPE) {
                    block = new DataBlock();
                } else {
                    block = new HeaderlessBlock();
                }
                block.setBlockLength(blockLength);
                block.setFlag(Flag.findByCode(code));
                block.read(is);
                add(block);
            }
        } catch (EOFException e) {
            log.info("eof");
        }
    }

    public void add(@NonNull final Block block) {
        blockList.add(block);
    }

    @NonNull
    public void write(@NonNull final OutputStream os) throws IOException {
        for (Block block : blockList) {
            block.write(os);
        }
    }

    @Override
    public void export(@NonNull OutputStream os) throws IOException {
        for (Block block : blockList) {
            block.export(os);
        }
    }

    public List<Block> find(@NonNull Flag flag) {
        final List<Block> findList = new LinkedList<>();
        for (Block block : blockList) {
            if (block.getFlag() == flag) {
                findList.add(block);
            }
        }
        return Collections.unmodifiableList(findList);
    }
}
