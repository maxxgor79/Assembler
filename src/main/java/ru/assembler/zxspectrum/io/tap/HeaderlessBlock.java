package ru.assembler.zxspectrum.io.tap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.LEDataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
public class HeaderlessBlock extends Block implements TapElementReader, TapElementWriter {
  private byte[] content;

  public void setContent(@NonNull byte[] content) {
    this.content = content;
    checkSum = calcCheckSum();
  }

  @Override
  protected int calcCheckSum() {
    int checkSum = 0;
    if (flag != null) {
      checkSum ^= flag.getCode();
    }
    if (content != null) {
      checkSum ^= TapUtil.calculateChecksum(content);
    }
    return checkSum;
  }

  @Override
  public int size() {
    return 2 + (content != null ? content.length : 0);
  }

  @Override
  public void read(@NonNull InputStream is) throws IOException {
    content = new byte[blockLength - 2];
    is.read(content);
    checkSum = is.read();
    int calculated = calcCheckSum();
    if (checkSum != calculated) {
      throw new IllegalStateException(
          "Bad checksum, loaded=" + checkSum + ", calculated=" + calculated);
    } else {
      if (log.isInfoEnabled()) {
        log.info("checkSum is OK");
      }
    }
  }

  @Override
  public void write(@NonNull OutputStream os) throws IOException {
    LEDataOutputStream dos = new LEDataOutputStream(os);
    dos.writeShort(blockLength);
    dos.write(flag.getCode());
    export(os);
  }

  @Override
  public void export(@NonNull OutputStream os) throws IOException {
    os.write(flag.getCode());
    if (content != null) {
      os.write(content);
    }
    os.write(checkSum = calcCheckSum());
  }
}
