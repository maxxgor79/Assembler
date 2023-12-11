package ru.zxspectrum.io.tap;

import java.io.InputStream;
import java.io.OutputStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;

@ToString
@EqualsAndHashCode
public class HeaderlessBlock extends Block implements TapElementReader, TapElementWriter {

  @Getter
  private byte[] content;

  public void setContent(@NonNull byte[] content) {
    this.content = content;
    checkSum = calcCheckSum();
  }

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
  public void read(@NonNull InputStream is) throws IOException {
    content = new byte[blockLength - 2];
    is.read(content);
    checkSum = is.read();
    int calculated = calcCheckSum();
    if (checkSum != calculated) {
      throw new IllegalStateException(
          "Bad checksum, loaded=" + checkSum + ", calculated=" + calculated);
    }
  }

  @Override
  public void write(@NonNull OutputStream os) throws IOException {
    LEDataOutputStream dos = new LEDataOutputStream(os);
    dos.writeShort(blockLength);
    dos.writeByte(flag.getCode());
    export(os);
  }

  @Override
  public void export(@NonNull OutputStream os) throws IOException {
    os.write(flag.getCode());
    if (content != null) {
      os.write(content);
    }
    os.write(checkSum);
  }
}
