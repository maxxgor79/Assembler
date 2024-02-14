package ru.zxspectrum.io.audio.reader;

import java.io.IOException;

public interface Interceptor {
    void save(byte[] buf, int size, int pause) throws IOException;
}
