package ru.zxspectrum.io.tap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.io.LEDataOutputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
@ToString(callSuper = true)
@EqualsAndHashCode
@Slf4j
@Getter
public class DataBlock extends Block implements Container {
  public static final int TYPE = 0xFF;

  @NonNull
  @Getter
  private byte[] content;

  public DataBlock() {
    setFlag(Flag.Data);
  }

  @Override
  public void setContent(@NonNull byte[] content) {
    if (this.content == content) {
      return;
    }
    this.content = content;
    blockLength = 2 + content.length;
    checkSum = calcCheckSum();
  }

  @Override
  public void read(@NonNull InputStream is) throws IOException {
    content = new byte[blockLength - 2];
    int readBytes = is.read(content);
    if (readBytes != content.length) {
      throw new EOFException();
    }
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
    export(dos);
  }

  @Override
  public void export(@NonNull OutputStream os) throws IOException {
    export(new LEDataOutputStream(os));
  }

  private void export(LEDataOutputStream dos) throws IOException {
    dos.write(flag.getCode());
    if (content != null) {
      dos.write(content);
    }
    dos.write(checkSum = calcCheckSum());
  }

  @Override
  protected int calcCheckSum() {
    int checkSum = 0;
    if (flag != null) {
      checkSum ^= flag.getCode();
    }
    if (content != null) {
      checkSum ^= TapUtils.calculateChecksum(content);
    }
    return checkSum;
  }

  @Override
  public int size() {
    return 2 + ((content != null) ? content.length : 0);
  }
}
