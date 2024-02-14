package ru.zxspectrum.io.tzx;

import lombok.NonNull;
import ru.zxspectrum.io.tap.Block;
import ru.zxspectrum.io.tap.TapData;
import ru.zxspectrum.io.tap.TapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Maxim Gorin
 */

public final class TzxUtils {
    public static final String EXTENSION = "tzx";

    private TzxUtils() {

    }

    public static void createTzx(@NonNull TapData tapData, @NonNull File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            final TzxWriter tzxWriter = new TzxWriter(fos);
            tzxWriter.writeHeader();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (Block block : tapData.getBlockList()) {
                baos.reset();
                block.export(baos);
                final byte[] blockData = baos.toByteArray();
                tzxWriter.writeID10(blockData, blockData.length, 0);
            }
        }
    }

    public static void createTzxFromTap(@NonNull File outputFile, @NonNull byte[] data, int address) throws IOException {
        final TapData tapData = TapUtils.createBinaryTap(data, address);
        createTzx(tapData, outputFile);
    }

    public static void toBytes(@NonNull TzxData tzxData, @NonNull OutputStream os) throws IOException {
        for (ru.zxspectrum.io.tzx.Block block : tzxData.getBlocks(DataBlock.DEFAULT_ID)) {
            block.write(os);
        }
    }
}
