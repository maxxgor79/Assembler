package ru.assembler.io.wav;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.LEDataInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class WavInputStream extends InputStream {
    public static final int PCM_FORMAT = 1;

    @Getter
    protected short bps;

    @Getter
    private int sampleRate;

    @Getter
    private int format;

    @Getter
    private int byteRate;

    @Getter
    private short blockAlign;

    @Getter
    private short numChannels;

    @Getter
    private int dataSize;

    @Getter
    private int estimatedDataSize;

    private LEDataInputStream leDis;

    public WavInputStream(@NonNull InputStream is) throws IOException {
        this.leDis = new LEDataInputStream(is);
        readHeader();
    }

    private void readHeader() throws IOException {
        final byte[] riff = new byte[4];
        leDis.read(riff);
        if (!"RIFF".equals(new String(riff))) {
            throw new IOException("Bad header ID:" + new String(riff));
        }
        dataSize = leDis.readInt() - 36;
        final byte[] wave = new byte[4];
        leDis.read(wave);
        if (!"WAVE".equals(new String(wave))) {
            throw new IOException("Bad chunk header ID:" + new String(wave));
        }
        final byte[] fmt = new byte[4];
        leDis.read(fmt);
        if (!"fmt ".equals(new String(fmt))) {
            throw new IOException("Bad chunk header ID:" + new String(fmt));
        }
        final int chunkSize = leDis.readInt();
        if (chunkSize != 16) {
            throw new IOException("Bad chunk size: " + chunkSize);
        }
        this.format = leDis.readShort();
        if (format != PCM_FORMAT) {
            throw new IOException("Unsupported format:" + format);
        }
        this.numChannels = leDis.readShort();
        this.sampleRate = leDis.readInt();
        this.byteRate = leDis.readInt();
        this.blockAlign = leDis.readShort();
        this.bps = leDis.readShort();
        final byte[] data = new byte[4];
        leDis.read(data);
        if (!"data".equals(new String(data))) {
            throw new IOException("Bad chunk header ID:" + new String(data));
        }
        dataSize = estimatedDataSize = leDis.readInt();
    }

    @Override
    public int read() throws IOException {
        if (estimatedDataSize > 0) {
            estimatedDataSize--;
            return leDis.read();
        }
        return -1;
    }

    public int readSample() throws IOException {
        int ch1, ch2;
        switch (bps) {
            case 8:
                return read();
            case 16:
                ch1 = read();
                ch2 = read();
                if ((ch1 | ch2) < 0) {
                    throw new EOFException();
                }
                return (ch2 << 8) + (ch1 << 0);
            default:
                throw new IOException("Unsupported format");
        }
    }

    public int[] readSamples() throws IOException {
        int ch1, ch2;
        switch (numChannels) {
            case 1:
                ch1 = readSample();
                return ch1 == -1 ? null : new int[]{ch1};
            case 2:
                ch1 = readSample();
                ch2 = readSample();
                if ((ch1 | ch2) < 0) {
                    throw new EOFException();
                }
                return new int[]{ch1, ch2};
            default:
                throw new IOException("Unsupported channel number");
        }
    }
}
