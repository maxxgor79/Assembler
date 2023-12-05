package ru.zxspectrum.io.tap;

import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;

public interface TapElementWriter {
    void write(LEDataOutputStream dos) throws IOException;
}
