package ru.zxspectrum.converter.core;

import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import ru.zxspectrum.io.audio.wav.WavInputStream;
import ru.zxspectrum.converter.i18n.ConvMessages;
import ru.zxspectrum.converter.text.Formatter;
import ru.zxspectrum.error.ConverterException;
import ru.zxspectrum.io.audio.generator.SignalGenerator;
import ru.zxspectrum.io.audio.reader.SignalReader;
import ru.zxspectrum.io.tap.TapData;
import ru.zxspectrum.io.tap.TapUtils;
import ru.zxspectrum.io.tzx.TzxData;
import ru.zxspectrum.io.tzx.TzxUtils;
import ru.zxspectrum.io.tzx.TzxWriter;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Converter {
    private Converter() {

    }

    protected static void checkExisting(@NonNull final File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(Formatter.format(ConvMessages.getMessage(ConvMessages.FILE_NOT_EXIST)
                    , file.getAbsolutePath()));
        }
    }

    public static void tap2wav(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        final TapData tapData = new TapData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tapData.read(fis);
        }
        final SignalGenerator sg = new SignalGenerator(outputFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tapData);
    }

    public static void tzx2wav(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        final TzxData tzxData = new TzxData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tzxData.read(fis);
        }
        final SignalGenerator sg = new SignalGenerator(outputFile);
        sg.setSilenceBeforeBlock(true);
        sg.generateWav(tzxData);
    }

    public static void wav2tzx(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             FileInputStream fis = new FileInputStream(inputFile)) {
            wav2tzx(fis, fos);
        }
    }

    public static void wav2tzx(@NonNull final InputStream is, @NonNull final OutputStream os) throws ConverterException
            , IOException {
        try {
            final WavInputStream wis = new WavInputStream(is);
            final SignalReader signalReader = new SignalReader(wis);
            final TzxWriter writer = new TzxWriter(os);
            writer.writeHeader();
            signalReader.setInterceptor((buf, size, pause) -> writer.writeID10(buf, size, pause));
            signalReader .readAll();
        } catch (UnsupportedAudioFileException e) {
            throw new ConverterException(e.getMessage(), e);
        }
    }

    public static void wav2tap(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            wav2tzx(fis, baos);
        }
        final byte[] data = baos.toByteArray();
        final TzxData tzxData = new TzxData();
        tzxData.read(new ByteArrayInputStream(data));
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            tzx2Tap(tzxData, fos);
        }
    }

    public static void wav2Raw(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        final WavInputStream wis = new WavInputStream(inputFile);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            final SignalReader signalReader = new SignalReader(wis);
            signalReader.setInterceptor((buf, size, pause) -> fos.write(buf, 0, size));
            signalReader.readAll();
        } catch (UnsupportedAudioFileException e) {
            throw new ConverterException(e.getMessage(), e);
        }
    }

    public static void raw2tap(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        throw new ConverterException(ConvMessages.getMessage(ConvMessages.UNSUPPORTED_CONVERSION));
    }

    public static void raw2tzx(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        throw new ConverterException(ConvMessages.getMessage(ConvMessages.UNSUPPORTED_CONVERSION));
    }

    public static void raw2wav(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        throw new ConverterException(ConvMessages.getMessage(ConvMessages.UNSUPPORTED_CONVERSION));
    }

    public static void unsupported(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        throw new ConverterException(ConvMessages.getMessage(ConvMessages.UNSUPPORTED_CONVERSION));
    }

    public static void copy(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            IOUtils.copy(fis, fos);
        }
    }

    public static void tzx2Tap(@NonNull final File inputFile, @NonNull final File outputFile) throws ConverterException
            , IOException {
        checkExisting(inputFile);
        final TzxData tzxData = new TzxData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tzxData.read(fis);
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            tzx2Tap(tzxData, fos);
        }
    }

    protected static void tzx2Tap(@NonNull final TzxData tzxData, @NonNull final OutputStream os) throws IOException
            , ConverterException {
        TapUtils.createTap(tzxData, os);
    }

    public static void tzx2Raw(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        final TzxData tzxData = new TzxData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tzxData.read(fis);
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            TzxUtils.toBytes(tzxData, fos);
        }
    }

    public static void tap2Raw(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        final TapData tapData = new TapData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tapData.read(fis);
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            TapUtils.toBytes(tapData, fos);
        }
    }

    public static void tap2tzx(@NonNull final File inputFile, @NonNull final File outputFile) throws IOException {
        checkExisting(inputFile);
        final TapData tapData = new TapData();
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            tapData.read(fis);
        }
        TzxUtils.createTzx(tapData, outputFile);
    }
}
