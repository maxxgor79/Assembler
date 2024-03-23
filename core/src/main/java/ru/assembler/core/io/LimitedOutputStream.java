package ru.assembler.core.io;

import java.io.IOException;
import java.io.OutputStream;

import lombok.Getter;
import lombok.NonNull;

/**
 * LimitedOutputStream.
 *
 * @author Maxim Gorin
 */
public class LimitedOutputStream extends OutputStream {

    @Getter
    private Limitation limitation;

    @Getter
    private int counter;

    @NonNull
    private OutputStream os;

    private LimitedOutputStream() {

    }

    public LimitedOutputStream(@NonNull final OutputStream os, final Limitation limitation) {
        this.os = os;
        this.limitation = limitation;
    }

    public LimitedOutputStream(@NonNull final OutputStream os, final int limit) {
        this.os = os;
        this.limitation = () -> limit;
    }

    private void checkLimit() throws IOException {
        if (limitation != null && counter >= limitation.getLimit()) {
            throw new IOException("Exceed limit at " + limitation.getLimit() + " bytes");
        }
    }

    public void write(@NonNull final byte[] b, final int off, final int len) throws IOException {
        os.write(b, off, len);
        counter += len;
        checkLimit();
    }

    @Override
    public void write(final int b) throws IOException {
        os.write(b);
        counter++;
        checkLimit();
    }

    @Override
    public void write(@NonNull final byte[] b) throws IOException {
        os.write(b);
        counter += b.length;
        checkLimit();
    }

    public void flush() throws IOException {
        os.flush();
    }

    public void close() throws IOException {
        os.close();
    }

    public interface Limitation {
        int getLimit();
    }
}
