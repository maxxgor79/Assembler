package ru.assembler.zxspectrum.io.tzx;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@ToString
public class TzxData extends Block {

  @Getter
  final private HeaderBlock headerBlock = new HeaderBlock();

  @Getter
  final private List<Block> blocks = new LinkedList<>();

  @Override
  public void read(@NonNull final InputStream is) throws IOException {
    final PushbackInputStream pis = new PushbackInputStream(is);
    headerBlock.read(pis);
    blocks.clear();
    boolean ret = false;
    while (!ret) {
      final int b = pis.read();
      if (b == -1) {
        break;
      }
      Block block;
      switch (b) {
        case DataBlock.DEFAULT_ID:
          pis.unread(b);
          block = new DataBlock();
          block.read(pis);
          blocks.add(block);
          break;
        case TextDescriptionBlock.DEFAULT_ID:
          pis.unread(b);
          block = new TextDescriptionBlock();
          block.read(pis);
          blocks.add(block);
          break;
        case PauseBlock.DEFAULT_ID:
          pis.unread(b);
          block = new PauseBlock();
          block.read(pis);
          blocks.add(block);
          break;
        case MessageBlock.DEFAULT_ID:
          pis.unread(b);
          block = new MessageBlock();
          block.read(pis);
          blocks.add(block);
          break;
        case 0x27:
          ret = true;
          break;
        default:
          throw new IOException("Unknown byte: " + b);
      }
    }

  }

  @Override
  public void write(@NonNull final OutputStream os) throws IOException {
    headerBlock.write(os);
    for (Block block : blocks) {
      block.write(os);
    }
  }

  public List<Block> getBlocks(int blockId) {
    final List<Block> resultList = new LinkedList<>();
    for (Block b : blocks) {
      if (b.blockId == blockId) {
        resultList.add(b);
      }
    }
    return resultList;
  }

  public void export(@NonNull OutputStream os) throws IOException {
    for (Block b : blocks) {
      b.export(os);
    }
  }
}
