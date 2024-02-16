package ru.assembler.core.io.image;

import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class BmpReader {
    protected static final String ID = "BM";

    @Getter
    protected int width;

    @Getter
    protected int height;

    @Getter
    protected int bps; //bits per pixel

    @Getter
    @NonNull
    protected byte[] content;

    public BmpReader(@NonNull final InputStream is) throws IOException {
        read(is);
    }

    public void read(@NonNull final InputStream is) throws IOException {
        readHeader(is);
        readContent(is);
    }

    protected void readHeader(@NonNull final InputStream is) throws IOException {

    }

    protected void readContent(@NonNull final InputStream is) throws IOException {

    }
}
