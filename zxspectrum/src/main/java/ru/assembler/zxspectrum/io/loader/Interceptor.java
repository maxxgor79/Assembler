package ru.assembler.zxspectrum.io.loader;

import java.io.IOException;

public interface Interceptor {
    void save(byte[] buf, int size, int pause) throws IOException;
}
