package ru.assembler.zxspectrum.io.generator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.generator.Generator;
import ru.assembler.io.wav.WavWriter;
import ru.assembler.zxspectrum.io.tap.Block;
import ru.assembler.zxspectrum.io.tap.Flag;
import ru.assembler.zxspectrum.io.tap.TapData;
import ru.assembler.zxspectrum.io.tzx.DataBlock;
import ru.assembler.zxspectrum.io.tzx.TzxData;

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

    private boolean silenceBeforeBlock = true;

    private int silenceDuration = 1;// in seconds

    public SignalGenerator() {
        setSampleRate(DEFAULT_SAMPLE_RATE);
    }

    public SignalGenerator(@NonNull File file) {
        setSampleRate(DEFAULT_SAMPLE_RATE);
        setFile(file);
    }

    protected void writeDataByte(@NonNull final OutputStream os, final int b, final int hi
            , final int lo, final int frequency) throws IOException {
        int mask = 0x80;
        while (mask != 0) {
            int len = ((b & mask) == 0) ? PULSE_ZERO : PULSE_ONE;
            writeSignal(os, hi, len, frequency);
            writeSignal(os, lo, len, frequency);
            mask >>= 1;
        }
    }

    protected int volumeToLevel(final float volume) {
        int volLevel = (int) ((1.00f - volume) * 0x40);
        if (volLevel < 0) {
            volLevel = 0;
        }
        if (volLevel > 0x40) {
            volLevel = 0x40;
        }
        return volLevel;
    }

    protected void writeSoundData(@NonNull final OutputStream os, @NonNull final byte[] data
            , final int sampleRate, final float volume, final boolean isHeader) throws IOException {
        final int volLevel = volumeToLevel(volume);
        final int hiLevel = 0xc0 - volLevel;
        final int loLevel = 0x40 + volLevel;
        int pilotImpulses;
        if (isHeader) {
            pilotImpulses = IMPULSE_NUMBER_PILOT_HEADER;
        } else {
            pilotImpulses = IMPULSE_NUMBER_PILOT_DATA;
        }
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

    @Override
    public void setVolume(float volume) {
        if (volume <= 0.0f) {
            volume = 0.0f;
        }
        if (volume >= 1.0f) {
            volume = 1.0f;
        }
        this.volume = volume;
    }

    protected void writeSilence(@NonNull final OutputStream os) throws IOException {
        for (int i = 0; i < (silenceDuration * sampleRate); i++) {
            os.write(0x80);
        }
    }

    @Override
    public void generateWav(@NonNull final Object data) throws IOException {
        if (data instanceof TapData) {
            generateWavFromTap((TapData) data);
        } else if (data instanceof TzxData) {
            generateWavFromTzx((TzxData) data);
        } else {
            throw new IllegalArgumentException("Unsupported argument type: " + data.getClass());
        }
    }

    protected void generateWavFromTzx(@NonNull final TzxData tzxData) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        for (final ru.assembler.zxspectrum.io.tzx.Block block : tzxData.getBlocks(DataBlock.DEFAULT_ID)) {
            if (silenceBeforeBlock) {
                writeSilence(baos);
            }
            final boolean isHeader = (i == 0) ? true : false;
            writeSoundData(baos, toBytes((DataBlock) block), sampleRate, volume, isHeader);
            i++;
        }
        writeSilence(baos);
        final WavWriter wavFile = new WavWriter(baos.toByteArray(), sampleRate, 1);// 11025 Hz
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wavFile.write(fos);
        }
    }

    protected void generateWavFromTap(@NonNull final TapData tapData) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (final Block block : tapData.getBlockList()) {
            if (silenceBeforeBlock) {
                writeSilence(baos);
            }
            final boolean isHeader = (block.getFlag() == Flag.Header) ? true : false;
            writeSoundData(baos, toBytes(block), sampleRate, volume, isHeader);
        }
        writeSilence(baos);
        final WavWriter wavFile = new WavWriter(baos.toByteArray(), sampleRate, 1);// 11025 Hz
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wavFile.write(fos);
        }
    }

    protected static byte[] toBytes(@NonNull final Block block) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        block.export(baos);
        return baos.toByteArray();
    }

    protected static byte[] toBytes(@NonNull final DataBlock block) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        block.export(baos);
        return baos.toByteArray();
    }
}
