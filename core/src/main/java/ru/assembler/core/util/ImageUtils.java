package ru.assembler.core.util;

import lombok.NonNull;
import org.apache.commons.io.output.ByteArrayOutputStream;
import ru.assembler.core.io.image.BmpReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class ImageUtils {
    private static final String OUTPUT_FORMAT = "bmp";

    private ImageUtils() {

    }

    public static BufferedImage brighten(@NonNull final BufferedImage image, final float level) {
        BufferedImage dst = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        float[] scales = {level, level, level};
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        Graphics2D g = dst.createGraphics();
        g.drawImage(image, rop, 0, 0);
        g.dispose();
        return dst;
    }

    public static BufferedImage toMonocrome(@NonNull final BufferedImage image) {
        final BufferedImage blackWhite = new BufferedImage(image.getWidth(), image.getHeight()
                , BufferedImage.TYPE_BYTE_BINARY);
        final Graphics2D g2d = blackWhite.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return blackWhite;
    }

    public static byte[] toBytes(@NonNull final BufferedImage image) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, OUTPUT_FORMAT, baos);
        final byte[] bmpData = baos.toByteArray();
        final BmpReader reader = new BmpReader(new ByteArrayInputStream(bmpData));
        if (reader.getBps() != 1) {
            throw new UnsupportedOperationException("Monochrome format is only supported");
        }
        if (reader.getCompression() != BmpReader.BI_RGB) {
            throw new UnsupportedOperationException("Compression is not supported");
        }
        final byte [] bitmap = reader.getContent();
        return bitmap;
    }
}
