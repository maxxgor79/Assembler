package ru.assembler.zxspectrum.io.tzx;

import lombok.NonNull;
import ru.assembler.zxspectrum.io.tap.Block;
import ru.assembler.zxspectrum.io.tap.TapData;
import ru.assembler.zxspectrum.io.tap.TapUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class TzxUtils {
    public static final String EXTENSION = "tzx";

    private TzxUtils() {

    }

    public static void createTzx(@NonNull File dst, @NonNull byte[] data, int address) throws IOException {
        final TapData tapData = TapUtil.createBinaryTap(data, address);
        try (FileOutputStream fos = new FileOutputStream(dst)) {
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
}
