package ru.assembler.zxspectrum.io.tzx;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */

public class TzxWriter {
    private OutputStream os;

    public TzxWriter(@NonNull OutputStream os) {
        this.os = os;
    }

    public void writeHeader() throws IOException {
        os.write("ZXTape!".getBytes());
        os.write(0x1a);
        os.write(1);  // major
        os.write(13); // minor
    }

    public void writeID10(@NonNull byte[] buf, int size, int pause) throws IOException {
        os.write(0x10); // block id
        writeShort(pause);
        writeShort(size);
        os.write(buf, 0, size);
    }

    private void writeShort(int val) throws IOException {
        os.write(val & 0xff);
        os.write((val >> 8) & 0xff);
    }
}
