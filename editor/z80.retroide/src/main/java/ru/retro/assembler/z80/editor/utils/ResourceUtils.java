package ru.retro.assembler.z80.editor.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @Author: Maxim Gorin Date: 20.02.2024
 */
@Slf4j
public final class ResourceUtils {
    private ResourceUtils() {

    }

    public static Image loadImage(@NonNull String path) throws IOException {
        final InputStream is = ResourceUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        final byte[] iconData = IOUtils.toByteArray(ResourceUtils.class.getResourceAsStream(path));
        final BufferedImage image = ImageIO.read(new ByteArrayInputStream(iconData));
        return image;
    }

    public static byte[] loadResource(@NonNull String path) throws IOException {
        final InputStream is = ResourceUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        return IOUtils.toByteArray(is);
    }

    public static Icon loadIcon(@NonNull String path) throws IOException {
        Image image = loadImage(path);
        return new ImageIcon(image);
    }

    public static String cutExtension(@NonNull final String path) {
        int index = FilenameUtils.indexOfExtension(path);
        if (index == -1) {
            return path;
        }
        return path.substring(0, index);
    }
}