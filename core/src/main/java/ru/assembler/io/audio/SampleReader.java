package ru.assembler.io.audio;

import java.io.IOException;

public interface SampleReader {
    int getSampleRate();

    int getBps();

    int getNumberChannels();

    int readSample() throws IOException;

    int[] readSamples() throws IOException;

}
