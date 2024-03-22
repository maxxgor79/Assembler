package ru.retro.assembler.i8080.editor.utils;

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
    byte[] iconData = IOUtils.toByteArray(ResourceUtils.class.getResourceAsStream(path));
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(iconData));
    return image;
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