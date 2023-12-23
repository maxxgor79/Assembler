package ru.zxspectrum.io.tap;

import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.LEDataInputStream;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Maxim Gorin
 */
@Slf4j
@ToString
public class BytesParams implements TapElementReader, TapElementWriter {

  @Setter
  @Getter
  private int startAddress;//2b

  @Setter
  @Getter
  private int reserved = 32768;//2b

  @Override
  public void read(@NonNull InputStream is) throws IOException {
    LEDataInputStream dis = new LEDataInputStream(is);
    startAddress = dis.readUnsignedShort();
    reserved = dis.readUnsignedShort();
  }

  @Override
  public void write(@NonNull final OutputStream dos) throws IOException {
    export(dos);
  }

  @Override
  public void export(OutputStream os) throws IOException {
    LEDataOutputStream dos = new LEDataOutputStream(os);
    dos.writeShort(startAddress);
    dos.writeShort(reserved);
  }

  public byte[] getBytes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      LEDataOutputStream dos = new LEDataOutputStream(baos);
      dos.writeShort(startAddress);
      dos.writeShort(reserved);
      return baos.toByteArray();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new byte[0];
    }
  }
}
