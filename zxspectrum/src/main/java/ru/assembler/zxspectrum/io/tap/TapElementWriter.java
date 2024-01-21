package ru.assembler.zxspectrum.io.tap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public interface TapElementWriter {
    void write(OutputStream os) throws IOException;

    void export(OutputStream os) throws IOException;
}
