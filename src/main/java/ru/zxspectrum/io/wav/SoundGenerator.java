package ru.zxspectrum.io.wav;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.io.LEDataOutputStream;
import ru.zxspectrum.io.tap.Block;
import ru.zxspectrum.io.tap.Flag;
import ru.zxspectrum.io.tap.TapData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SoundGenerator {
    protected static final int PULSE_ZERO = 855;

    protected static final int PULSE_ONE = 1710;

    protected static final int PULSE_PILOT = 2168;

    protected static final int PULSE_SYNC1 = 667;

    protected static final int PULSE_SYNC2 = 735;

    protected static final int PULSE_SYNC3 = 954;

    protected static final int IMPULSE_NUMBER_PILOT_HEADER = 8063;

    protected static final int IMPULSE_NUMBER_PILOT_DATA = 3223;

    @NonNull
    @Setter
    @Getter
    private File file;

    @Getter
    @Setter
    private boolean silenceBeforeBlock;

    @Setter
    @Getter
    private int silenceDuration = 1;//in seconds

    @Setter
    @Getter
    private int sampleRate = 22050;

    @Getter
    private float volume = 1.0f;

    public SoundGenerator() {

    }

    public SoundGenerator(@NonNull File file) {
        setFile(file);
    }

    protected static void writeDataByte(OutputStream os, int b, int hi, int lo, int frequency) throws IOException {
        int mask = 0x80;
        while (mask != 0) {
            int len = ((b & mask) == 0) ? PULSE_ZERO : PULSE_ONE;
            Signal.writeSignal(os, hi, len, frequency);
            Signal.writeSignal(os, lo, len, frequency);
            mask >>= 1;
        }
    }

    protected static void writeSoundData(OutputStream os, Block block, int sampleRate, float volume) throws IOException {
        int volLevel = (int) ((1.00f - volume) * 0x40);
        if (volLevel < 0) {
            volLevel = 0;
        }
        if (volLevel > 0x40) {
            volLevel = 0x040;
        }
        int hiLevel = 0xFF - volLevel;
        int loLevel = 0x00 + volLevel;

        byte[] data = null;
        int pilotImpulses;
        if (block.getFlag() == Flag.Header) {
            pilotImpulses = IMPULSE_NUMBER_PILOT_HEADER;
        } else {
            pilotImpulses = IMPULSE_NUMBER_PILOT_DATA;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        block.writeTap(new LEDataOutputStream(baos));
        data = baos.toByteArray();

        int signalLevel = hiLevel;
        for (int i = 0; i < pilotImpulses; i++) {
            Signal.writeSignal(os, signalLevel, PULSE_PILOT, sampleRate);
            signalLevel = signalLevel == hiLevel ? loLevel : hiLevel;
        }
        if (signalLevel == loLevel) {
            Signal.writeSignal(os, loLevel, PULSE_PILOT, sampleRate);
        }
        Signal.writeSignal(os, hiLevel, PULSE_SYNC1, sampleRate);
        Signal.writeSignal(os, loLevel, PULSE_SYNC2, sampleRate);
        for (int b : data) {
            writeDataByte(os, b, hiLevel, loLevel, sampleRate);
        }
        Signal.writeSignal(os, hiLevel, PULSE_SYNC3, sampleRate);
    }

    public void setVolume(float volume) {
        if (volume <= 0.0f) {
            volume = 0.0f;
        }
        if (volume >= 1.0f) {
            volume = 1.0f;
        }
        this.volume = volume;
    }

    protected void writeSilence(OutputStream os) throws IOException {
        for (int i = 0; i < (silenceDuration * sampleRate); i++) {
            os.write(0x80);
        }
    }

    public void generateWav(@NonNull TapData tapData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Block block : tapData.getBlockList()) {
            if (silenceBeforeBlock) {
                writeSilence(baos);
            }
            writeSoundData(baos, block, sampleRate, volume);
        }
        WavFile wavFile = new WavFile(baos.toByteArray(), sampleRate, 1);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wavFile.write(fos);
        }
    }
}
