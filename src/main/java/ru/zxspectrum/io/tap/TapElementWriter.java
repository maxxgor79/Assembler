package ru.zxspectrum.io.tap;

import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public interface TapElementWriter {
    void write(LEDataOutputStream dos) throws IOException;

    void writeTap(LEDataOutputStream dos) throws IOException;
}
