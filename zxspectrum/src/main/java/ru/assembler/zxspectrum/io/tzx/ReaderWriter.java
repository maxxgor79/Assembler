package ru.assembler.zxspectrum.io.tzx;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class ReaderWriter {
    public abstract void read(@NonNull InputStream is) throws IOException;

    public abstract void write(@NonNull OutputStream os) throws IOException;
 }
