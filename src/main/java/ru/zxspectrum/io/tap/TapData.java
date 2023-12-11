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

@Slf4j
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TapData implements TapElementReader, TapElementWriter {

  @Getter
  private List<Block> blockList = new ArrayList<>();

  @Override
  public void read(@NonNull InputStream is) throws IOException {
    LEDataInputStream dis = new LEDataInputStream(is);
    try {
      Block block;
      while (true) {
        int blockLength = dis.readUnsignedShort();
        int code = dis.read();
        switch (code) {
          case HeaderBlock.TYPE -> {
            block =  new HeaderBlock();
            block.setBlockLength(blockLength);
            block.setFlag(Flag.findByCode(code));
          }
          case DataBlock.TYPE -> {
            block = new DataBlock();
            block.setBlockLength(blockLength);
            block.setFlag(Flag.findByCode(code));
          }
          default -> {
            block = new HeaderlessBlock();
            block.setFlag(Flag.Data);
            block.setBlockLength(blockLength);
          }
        }
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
    List<Block> findList = new LinkedList<>();
    for (Block block : blockList) {
      if (block.getFlag() == flag) {
        findList.add(block);
      }
    }
    return Collections.unmodifiableList(findList);
  }
}
