package ru.zxspectrum.io.tap;

import java.io.InputStream;
import lombok.NonNull;
import ru.zxspectrum.io.LEDataInputStream;

import java.io.IOException;

public interface TapElementReader {
    void read(InputStream dis) throws IOException;
}
