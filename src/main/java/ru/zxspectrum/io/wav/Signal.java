package ru.zxspectrum.io.wav;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */
public final class Signal {
    public static double round(double value) {
        if (value < 0.0) {
            value -= 0.5;
        } else {
            value += -0.5;
        }
        return value;
    }

    public static void writeSignal(OutputStream os, int signalLevel, int clocks, int frequency) throws IOException {
        double sampleNanoSec = 1_000_000_000.0 / frequency;
        double cpuClockNanoSec = 286;
        int numSamples = (int) round((cpuClockNanoSec * (double) clocks) / sampleNanoSec);
        for (int i = 0; i < numSamples; i++) {
            os.write(signalLevel);
        }
    }
}
