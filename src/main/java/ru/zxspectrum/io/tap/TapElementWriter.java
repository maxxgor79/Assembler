package ru.zxspectrum.io.tap;

import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public interface TapElementWriter {
    void write(OutputStream os) throws IOException;

    void export(OutputStream os) throws IOException;
}
