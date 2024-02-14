package ru.zxspectrum.io.audio.wav;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.io.audio.SampleReader;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class WavResamplerInputStream extends InputStream implements SampleReader {
    @Getter
    private int sampleRate;

    @Getter
    private int bps;

    private WavInputStream wis;

    private double ratio;

    private double sampledFrameSize;

    private double currentSampledFrameSize;

    private final int[] buffer = new int[8];

    private int previousSample;

    private int sampleIndex;

    public WavResamplerInputStream(@NonNull WavInputStream wis, int newSampleRate) throws UnsupportedAudioFileException {
        this(wis, wis.getSampleRate(), newSampleRate);
    }

    public WavResamplerInputStream(@NonNull WavInputStream wis, int newSampleRate, int newBps) throws
            UnsupportedAudioFileException {
        if (wis.getNumberChannels() != 1) {
            throw new UnsupportedAudioFileException("Only mono format supported");
        }
        if ((wis.getBps() != 8 && wis.getBps() != 16) ||
                (newBps != 8 && newBps != 16)) {
            throw new UnsupportedAudioFileException("Only 8 or 16 sample bits supported");
        }
        if (wis.getSampleRate() < 8000 || wis.getSampleRate() > 96000) {
            throw new UnsupportedAudioFileException("Bad rate: " + wis.getSampleRate());
        }
        this.wis = wis;
        this.sampleRate = newSampleRate;
        this.bps = newBps;
        this.ratio = (double) wis.getSampleRate() / (double) newSampleRate;
        this.sampledFrameSize = Math.max(wis.getSampleRate(), (double) newSampleRate)
                / Math.min(wis.getSampleRate(), (double) newSampleRate);
    }

    @Override
    public int read() throws IOException {
        return wis.read();
    }

    protected int readConvertedSample() throws IOException {
        int sample = wis.readSample();
        if (wis.getBps() == bps) {
            return sample;
        }
        if (wis.getBps() == 16 && bps == 8) {
            return (sample >> 8) & 0xff;
        }
        return ((sample + 0x80) & 0xff) << 8;
    }

    @Override
    public int getNumberChannels() {
        return 1;
    }

    @Override
    public int readSample() throws IOException {
        if (wis.getSampleRate() == sampleRate) {
            return readConvertedSample();
        }
        if (wis.getSampleRate() > sampleRate) {
            return readSampleLoweredRate();
        }
        return readSampleRaisedRate();
    }

    private int clip(int value) {
        switch (bps) {
            case 8:
                if (value < -128) value = 128;
                if (value > 127) value = 127;
                break;
            case 16:
                if (value < -32768) value = -32768;
                if (value > 32767) value = 32767;
                break;
        }
        return value;
    }

    private int readSampleRaisedRate() throws IOException {
        if (sampleIndex >= (int) currentSampledFrameSize) {
            currentSampledFrameSize -= sampleIndex;
            currentSampledFrameSize += sampledFrameSize;
            sampleIndex = 0;
            final int frameSize = (int) currentSampledFrameSize;
            int readSample = 0;
            switch (bps) {
                case 8:
                    readSample = (byte) readConvertedSample();
                    break;
                case 16:
                    readSample = (short) readConvertedSample();
                    break;
            }
            if (frameSize == 1) {
                buffer[0] = readSample;
            } else {
                final double d = ((double) readSample - previousSample) / (double) frameSize;
                double sample = previousSample;
                for (int i = 0; i < frameSize; i++) {
                    sample += d;//interpolation
                    buffer[i] = clip((int) sample);
                }
            }
            previousSample = readSample;
        }
        return buffer[sampleIndex++];
    }

    private int readSampleLoweredRate() throws IOException {
        final int frameSize = (int) (currentSampledFrameSize += ratio);
        for (int i = 0; i < frameSize; i++) {
            buffer[i] = readConvertedSample();
        }
        currentSampledFrameSize -= frameSize;
        return extrapolate(frameSize);
    }

    private int extrapolate(final int frameSize) {
        if (frameSize == 1) {
            return buffer[0];
        }
        int value = 0;
        for (int i = 0; i < frameSize; i++) {
            switch (bps) {
                case 8:
                    value += (byte) buffer[i];
                    break;
                case 16:
                    value += (short) buffer[i];
                    break;
            }
        }
        return value / frameSize;
    }

    @Override
    public int[] readSamples() throws IOException {
        throw new UnsupportedOperationException();
    }
}
