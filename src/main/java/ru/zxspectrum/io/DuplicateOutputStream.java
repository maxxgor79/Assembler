package ru.zxspectrum.io;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public class DuplicateOutputStream extends OutputStream {
    private final OutputStream[] streams;
    public DuplicateOutputStream(@NonNull OutputStream ... streams) {
        this.streams = streams;
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        for (OutputStream os : streams) {
            os.write(b);
        }
        super.write(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        for (OutputStream os : streams) {
            os.write(b, off, len);
        }
        super.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream os : streams) {
            os.flush();
        }
        super.flush();
    }

    @Override
    public void close() throws IOException {
        for (OutputStream os : streams) {
            os.close();
        }
        super.close();
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream os : streams) {
            os.write(b);
        }
    }
}
