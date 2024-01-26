package ru.assembler.zxspectrum.io.generator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.generator.Generator;
import ru.assembler.zxspectrum.io.tap.Block;
import ru.assembler.zxspectrum.io.tap.Flag;
import ru.assembler.zxspectrum.io.tap.TapData;
import ru.assembler.io.wav.WavFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
@Slf4j
@Getter
@Setter
public class SignalGenerator extends Generator implements Signal {
    @NonNull
    private File file;

    private boolean silenceBeforeBlock;

    private int silenceDuration = 1;// in seconds

    public SignalGenerator() {
        setSampleRate(DEFAULT_SAMPLE_RATE);
    }

    public SignalGenerator(@NonNull File file) {
        setSampleRate(DEFAULT_SAMPLE_RATE);
        setFile(file);
    }

    protected void writeDataByte(OutputStream os, int b, int hi, int lo, int frequency) throws IOException {
        int mask = 0x80;
        while (mask != 0) {
            int len = ((b & mask) == 0) ? PULSE_ZERO : PULSE_ONE;
            writeSignal(os, hi, len, frequency);
            writeSignal(os, lo, len, frequency);
            mask >>= 1;
        }
    }

    protected void writeSoundData(OutputStream os, Block block, int sampleRate, float volume) throws IOException {
        int volLevel = (int) ((1.00f - volume) * 0x40);
        if (volLevel < 0) {
            volLevel = 0;
        }
        if (volLevel > 0x40) {
            volLevel = 0x40;
        }
        int hiLevel = 0xc0 - volLevel;
        int loLevel = 0x40 + volLevel;

        byte[] data;
        int pilotImpulses;
        if (block.getFlag() == Flag.Header) {
            pilotImpulses = IMPULSE_NUMBER_PILOT_HEADER;
        } else {
            pilotImpulses = IMPULSE_NUMBER_PILOT_DATA;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        block.export(baos);
        data = baos.toByteArray();

        int signalLevel = hiLevel;
        for (int i = 0; i < pilotImpulses; i++) {
            writeSignal(os, signalLevel, PULSE_PILOT, sampleRate);
            signalLevel = signalLevel == hiLevel ? loLevel : hiLevel;
        }
        if (signalLevel == loLevel) {
            writeSignal(os, loLevel, PULSE_PILOT, sampleRate);
        }
        writeSignal(os, hiLevel, PULSE_SYNC1, sampleRate);
        writeSignal(os, loLevel, PULSE_SYNC2, sampleRate);
        for (int b : data) {
            writeDataByte(os, b, hiLevel, loLevel, sampleRate);
        }
        writeSignal(os, hiLevel, PULSE_SYNC3, sampleRate);
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

    @Override
    public void generateWav(@NonNull Object data) throws IOException {
        if (!(data instanceof TapData)) {
            throw new IllegalArgumentException("Unsupported argument type: " + data.getClass());
        }
        final TapData tapData = (TapData) data;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Block block : tapData.getBlockList()) {
            if (silenceBeforeBlock) {
                writeSilence(baos);
            }
            writeSoundData(baos, block, sampleRate, volume);
        }
        writeSilence(baos);
        WavFile wavFile = new WavFile(baos.toByteArray(), sampleRate, 1);// 11025 Hz
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wavFile.write(fos);
        }
    }
}
