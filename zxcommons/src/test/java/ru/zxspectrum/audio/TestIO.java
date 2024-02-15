package ru.zxspectrum.audio;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zxspectrum.io.audio.wav.WavInputStream;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Maxim Gorin
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestIO {
    @Test
    public void testWav1() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/test/java/ru/zxspectrum/audio/8mono.wav")) {
            WavInputStream wis = new WavInputStream(fis);
            Assertions.assertEquals(wis.getBps(), 8);
            Assertions.assertEquals(wis.getNumberChannels(), 1);
            Assertions.assertEquals(wis.getSampleRate(),8000);
            Assertions.assertEquals(wis.getDataSize(), 8000);
            int b, readBytes = 0;
            while((b = wis.read()) != -1) {
                readBytes++;
            }
            Assertions.assertEquals(readBytes, 8000);
        }
    }

    @Test
    public void testWav2() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/test/java/ru/zxspectrum/audio/16mono.wav")) {
            WavInputStream wis = new WavInputStream(fis);
            Assertions.assertEquals(wis.getBps(), 16);
            Assertions.assertEquals(wis.getNumberChannels(), 1);
            Assertions.assertEquals(wis.getSampleRate(),11025);
            Assertions.assertEquals(wis.getDataSize(), 22050);
            int b, readBytes = 0;
            while((b = wis.read()) != -1) {
                readBytes++;
            }
            Assertions.assertEquals(readBytes, 22050);
        }
    }

    @Test
    public void testWav3() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/test/java/ru/zxspectrum/audio/8stereo.wav")) {
            WavInputStream wis = new WavInputStream(fis);
            Assertions.assertEquals(wis.getBps(), 8);
            Assertions.assertEquals(wis.getNumberChannels(), 2);
            Assertions.assertEquals(wis.getSampleRate(),8000);
            Assertions.assertEquals(wis.getDataSize(), 16000);
            int []couple = null;
            int readBytes = 0;
            try {
                while ((couple = wis.readSamples()) != null) {
                    readBytes++;
                }
            } catch(EOFException e) {

            }
            Assertions.assertEquals(readBytes, 8000);
        }
    }
}
