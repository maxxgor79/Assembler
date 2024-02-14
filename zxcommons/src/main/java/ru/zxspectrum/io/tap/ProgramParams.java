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
@ToString
@Slf4j
@Setter
@Getter
public class ProgramParams implements TapElementReader, TapElementWriter {
  private int autostartLine;//2b

  private int programSize;//2b

  @Override
  public void read(@NonNull InputStream is) throws IOException {
    final LEDataInputStream dis = new LEDataInputStream(is);
    autostartLine = dis.readUnsignedShort();
    programSize = dis.readUnsignedShort();
  }

  @Override
  public void write(@NonNull final OutputStream os) throws IOException {
    export(os);
  }

  @Override
  public void export(OutputStream os) throws IOException {
    final LEDataOutputStream dos = new LEDataOutputStream(os);
    dos.writeShort(autostartLine);
    dos.writeShort(programSize);
  }

  public byte[] getBytes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      final LEDataOutputStream dos = new LEDataOutputStream(baos);
      dos.writeShort(autostartLine);
      dos.writeShort(programSize);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new byte[0];
    }
    return baos.toByteArray();
  }
}
