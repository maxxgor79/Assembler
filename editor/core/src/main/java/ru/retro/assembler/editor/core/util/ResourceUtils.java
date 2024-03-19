package ru.retro.assembler.editor.core.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

  private static Icon questionIcon;

  private static Icon errorIcon;

  private static Icon informationIcon;

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

  public static Icon getQuestionIcon() {
    if (questionIcon == null) {
      try {
        questionIcon = loadIcon("/icon48x48/question.png");
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    return questionIcon;
  }

  public static Icon getErrorIcon() {
    if (errorIcon == null) {
      try {
        errorIcon = loadIcon("/icon48x48/error.png");
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    return errorIcon;
  }

  public static Icon getInformationIcon() {
    if (informationIcon == null) {
        try {
            informationIcon = loadIcon("/icon48x48/information.png");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    return informationIcon;
  }
}
