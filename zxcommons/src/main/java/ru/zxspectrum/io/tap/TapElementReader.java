package ru.zxspectrum.io.tap;

import java.io.InputStream;

import java.io.IOException;

/**
 * @author Maxim Gorin
 */
public interface TapElementReader {
    void read(InputStream dis) throws IOException;
}
