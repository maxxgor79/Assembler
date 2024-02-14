package ru.assembler.io.wav;

import java.io.IOException;

public interface WavSampleReader {
    int readSample() throws IOException;

    int[] readSamples() throws IOException;

}
