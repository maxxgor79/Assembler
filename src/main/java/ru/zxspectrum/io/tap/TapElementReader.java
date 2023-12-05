package ru.zxspectrum.io.tap;

import lombok.NonNull;
import ru.zxspectrum.io.LEDataInputStream;

import java.io.IOException;

public interface TapElementReader {
    void read(LEDataInputStream dis) throws IOException;
}
