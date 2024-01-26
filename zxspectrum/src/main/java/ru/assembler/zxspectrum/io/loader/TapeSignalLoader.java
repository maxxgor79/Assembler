package ru.assembler.zxspectrum.io.loader;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.assembler.zxspectrum.io.generator.Signal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TapeSignalLoader implements Signal {
    @Setter
    @Getter
    private int bps;

    @Setter
    @Getter
    private int numChannels;

    @Setter
    @Getter
    private int sampleRate;

    public TapeSignalLoader(int sampleRate, int bps, int numChannels) {
        if (sampleRate < 8000) {
            throw new IllegalArgumentException("Bad sampleRate value=" + sampleRate);
        }
        this.sampleRate = sampleRate;
        if (bps != 8 && bps != 16) {
            throw new IllegalArgumentException("Bad bps value: " + bps);
        }
        this.bps = bps;
        if (numChannels != 1 && numChannels != 2) {
            throw new IllegalArgumentException("Bad numChannels value:" + numChannels);
        }
        this.numChannels = numChannels;
    }

    public byte[] read(@NonNull InputStream is) throws IOException {
        if (numChannels > 1) {
            throw new UnsupportedOperationException("Stereo mode is not supported");
        }
        if (bps == 8) {
            return read8bps(is);
        }
        return read16bps(is);
    }

    private byte[] read16bps(InputStream is) {
        return null;
    }

    private byte[] read8bps(InputStream is) {
        return null;
    }
}
