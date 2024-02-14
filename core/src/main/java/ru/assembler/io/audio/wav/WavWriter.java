package ru.assembler.io.audio.wav;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.io.LEDataOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public class WavWriter {
    public static final String EXTENSION = "wav";

    public static final int DEFAULT_RATE = 11025;

    protected static final int PCM_FORMAT = 1;

    @Setter
    @Getter
    protected int bps = 8;

    private final byte[] data;

    @Getter
    @Setter
    private int sampleRate = DEFAULT_RATE;

    @Getter
    private int numChannels = 1;

    public WavWriter(@NonNull byte[] data) {
        this.data = data;
    }

    public WavWriter(@NonNull byte[] data, int sampleRate, int numChannels) {
        this.data = data;
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;
    }

    public WavWriter(@NonNull byte[] data, int sampleRate, int bps, int numChannels) {
        this.data = data;
        this.sampleRate = sampleRate;
        this.bps = bps;
        this.numChannels = numChannels;
    }

    protected void writeHeader(@NonNull LEDataOutputStream os, int size) throws IOException {
        os.write("RIFF".getBytes());
        os.writeInt(size);
        os.write("WAVE".getBytes());
    }

    protected void writeWavChunkHeader(@NonNull LEDataOutputStream os, int size) throws IOException {
        os.write("data".getBytes());
        os.writeInt(size);
    }

    protected void writeWav(@NonNull LEDataOutputStream os, byte[] data) throws IOException {
        writeHeader(os, data.length + 36);
        os.write("fmt ".getBytes());//ID
        os.writeInt(16);//size
        os.writeShort(PCM_FORMAT);//audio format
        os.writeShort(numChannels);//num channels
        os.writeInt(sampleRate);//sample rate
        os.writeInt((sampleRate * bps * numChannels) / 8);//byte rate
        os.writeShort((bps * numChannels) / 8);//block align
        os.writeShort(bps);//Bit per sample
        writeWavChunkHeader(os, data.length);
        os.write(data);
    }

    public void write(@NonNull OutputStream os) throws IOException {
        LEDataOutputStream dos = new LEDataOutputStream(os);
        writeWav(dos, data);
    }
}
