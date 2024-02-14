package ru.zxspectrum.io.tzx;

/**
 * TzxElementReader.
 *
 * @author Maxim Gorin
 */

import java.io.IOException;
import java.io.InputStream;

public interface TzxElementReader {

  void read(InputStream is) throws IOException;
}
