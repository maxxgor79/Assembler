package ru.zxspectrum.assembler.resource;

import java.io.InputStream;
import lombok.NonNull;

/**
 * Loader.
 *
 * @author Maxim Gorin
 */
public final class Loader {

  private Loader() {

  }

  public static InputStream openRoot(@NonNull String resourceName) {
    if (resourceName.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    return Loader.class.getResourceAsStream("/" + resourceName);
  }

  public static InputStream openBasic(@NonNull String resourceName) {
    if (resourceName.trim().isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }
    return Loader.class.getResourceAsStream("/basic/" + resourceName);
  }
}
