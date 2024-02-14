package ru.zxspectrum.io.tzx;

import java.io.IOException;
import java.io.OutputStream;
import lombok.NonNull;

/**
 * TzxElementWriter.
 *
 * @author Maxim Gorin
 */
public interface TzxElementWriter {

  void write(@NonNull OutputStream os) throws IOException;

  void export(@NonNull OutputStream os) throws IOException;
}
