package ru.retro.assembler.editor.core.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Date;

/**
 * @Author: Maxim Gorin Date: 02.03.2024
 */
@Slf4j
public class BuildVersionReader {

  @Getter
  private long buildVersion;

  @Getter
  private Date buildDate;

  public void loadFromResource(@NonNull String path) {
    try {
      final String line = new String(IOUtils.resourceToByteArray(path));
      final String[] parsed = line.split(" ");
      if (parsed.length >= 1 && parsed[0] != null) {
        buildVersion = Long.parseLong(parsed[0]);
      }
      if (parsed.length >= 2 && parsed[1] != null) {
        buildDate = new Date(Long.parseLong(parsed[1]));
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      buildVersion = 0;
      buildDate = null;
    }
  }
}
