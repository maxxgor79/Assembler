package ru.zxspectrum.io.tap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TapData implements TapElementReader, TapElementWriter {
    @Getter
    private List<Block> blockList = new LinkedList<>();

    @Override
    public void read(@NonNull LEDataInputStream dis) throws IOException {
        try {
            while (true) {
                Block block = new Block();
                block.read(dis);
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
    public void write(@NonNull final LEDataOutputStream dos) throws IOException {
        for (Block block : blockList) {
            block.write(dos);
        }
    }

    @Override
    public void writeTap(@NonNull LEDataOutputStream dos) throws IOException {
        for (Block block : blockList) {
            block.writeTap(dos);
        }
    }

    public List<Block> find(@NonNull Flag flag) {
        List<Block> findList = new LinkedList<>();
        for (Block block : blockList) {
            if (block.getFlag() == flag) {
                findList.add(block);
            }
        }
        return Collections.unmodifiableList(findList);
    }
}
