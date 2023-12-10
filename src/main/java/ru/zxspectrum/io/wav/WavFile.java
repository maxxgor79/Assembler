package ru.zxspectrum.io.wav;

import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.io.LEDataOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class WavFile {
    protected static final int PCM_FORMAT = 1;

    protected static final int BPS = 8;

    private byte[] data;

    @Getter
    private int sampleRate = 11025;

    @Getter
    private int numChannels = 1;

    public WavFile(@NonNull byte[] data) {
        this.data = data;
    }

    public WavFile(@NonNull byte[] data, int sampleRate, int numChannels) {
        this.data = data;
        this.sampleRate = sampleRate;
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
        os.writeInt((sampleRate * BPS * numChannels) / 8);//byte rate
        os.writeShort((BPS * numChannels) / 8);//block align
        os.writeShort(BPS);//Bit per sample
        writeWavChunkHeader(os, data.length);
        os.write(data);
    }

    public void write(@NonNull OutputStream os) throws IOException {
        LEDataOutputStream dos = new LEDataOutputStream(os);
        writeWav(dos, data);
    }
}
