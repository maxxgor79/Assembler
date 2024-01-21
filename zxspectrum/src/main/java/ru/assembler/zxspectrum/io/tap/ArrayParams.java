package ru.assembler.zxspectrum.io.tap;

import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.LEDataInputStream;
import ru.assembler.io.LEDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Arrays;

/**
 * @author Maxim Gorin
 */
@ToString
@Slf4j
@Getter
@Setter
public class ArrayParams implements TapElementReader, TapElementWriter {
  private int reserved1;//1b

  private int varName;//1b

  @NonNull
  private byte[] reserved2 = {0, -128};

  @Override
  public void read(@NonNull final InputStream is) throws IOException {
    LEDataInputStream dis = new LEDataInputStream(is);
    reserved1 = dis.readUnsignedByte();
    varName = dis.readUnsignedByte();
    dis.readFully(reserved2, 0, 2);
    if (!Arrays.equals(reserved2, new byte[]{0, -128})) {
      throw new InvalidObjectException("Bad validation: " + Arrays.toString(reserved2));
    }
  }

  @Override
  public void write(@NonNull final OutputStream dos) throws IOException {
    export(dos);
  }

  @Override
  public void export(OutputStream os) throws IOException {
    os.write(reserved1);
    os.write(varName);
    os.write(reserved2);
  }

  public byte[] getBytes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      LEDataOutputStream dos = new LEDataOutputStream(baos);
      dos.writeByte(reserved1);
      dos.writeByte(varName);
      dos.write(reserved2);
      return baos.toByteArray();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new byte[0];
    }
  }
}
