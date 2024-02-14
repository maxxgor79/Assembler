package ru.assembler.zxspectrum.io.audio.generator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public interface Signal {
    int DEFAULT_SAMPLE_RATE = 22050;

    int PULSE_ZERO = 855;

    int PULSE_ONE = 1710;

    int PULSE_PILOT = 2168;

    int PULSE_SYNC1 = 667;

    int PULSE_SYNC2 = 735;

    int PULSE_SYNC3 = 954;

    int IMPULSE_NUMBER_PILOT_HEADER = 8063;

    int IMPULSE_NUMBER_PILOT_DATA = 3223;

    default double round(double value) {
        if (value < 0.0) {
            value -= 0.5;
        } else {
            value += -0.5;
        }
        return value;
    }

    default void writeSignal(OutputStream os, int signalLevel, int clocks, int frequency) throws IOException {
        double sampleNanoSec = 1_000_000_000.0 / frequency;
        double cpuClockNanoSec = 286;
        int numSamples = (int) round((cpuClockNanoSec * (double) clocks) / sampleNanoSec);
        for (int i = 0; i < numSamples; i++) {
            os.write(signalLevel);
        }
    }
}
