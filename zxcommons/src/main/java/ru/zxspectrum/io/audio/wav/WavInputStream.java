package ru.zxspectrum.io.audio.wav;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.assembler.core.io.LEDataInputStream;
import ru.zxspectrum.io.audio.SampleReader;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Maxim Gorin
 */

@Slf4j
public class WavInputStream extends InputStream implements SampleReader {
    public static final int PCM_FORMAT = 1;

    @Getter
    protected short bps;

    @Getter
    protected int sampleRate;

    @Getter
    private int format;

    @Getter
    private int byteRate;

    @Getter
    private short blockAlign;

    @Getter
    protected int numberChannels;

    @Getter
    private int dataSize;

    @Getter
    private int estimatedDataSize;

    private LEDataInputStream leDis;

    private int[] samples;

    protected WavInputStream() {

    }

    public WavInputStream(@NonNull InputStream is) throws IOException {
        this.leDis = new LEDataInputStream(is);
        readHeader();
    }

    public WavInputStream(@NonNull final File file) throws IOException {
        byte[] data;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = IOUtils.toByteArray(fis);
        }
        this.leDis = new LEDataInputStream(new ByteArrayInputStream(data));
        readHeader();
    }

    public WavInputStream(@NonNull final String filename) throws IOException {
        this(new File(filename));
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
        this.numberChannels = leDis.readShort();
        this.samples = new int[this.numberChannels];
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

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public int getBps() {
        return bps;
    }

    @Override
    public int getNumberChannels() {
        return numberChannels;
    }

    @Override
    public int readSample() throws IOException {
        int ch1, ch2;
        switch (bps) {
            case 8:
                ch1 = read();
                if (ch1 == -1) {
                    throw new EOFException();
                }
                return ch1;
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

    @Override
    public int[] readSamples() throws IOException {
        switch (numberChannels) {
            case 1:
                samples[0] = readSample();
                break;
            case 2:
                samples[0] = readSample();
                samples[1] = readSample();
                break;
            default:
                throw new IOException("Unsupported channel number");
        }
        return samples;
    }
}
