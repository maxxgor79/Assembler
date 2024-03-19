package ru.retro.assembler.editor.core.io;

import java.io.InputStreamReader;
import java.io.Reader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: Maxim Gorin Date: 28.02.2024
 */
@Slf4j
public class ConsoleWriter implements Runnable {

  private final Reader reader;

  private final JTextArea textArea;

  public ConsoleWriter(@NonNull final InputStream is, @NonNull final JTextArea textArea) {
    this.reader = new InputStreamReader(is);
    this.textArea = textArea;
  }

  @Override
  public void run() {
    final char[] buffer = new char[256];
    int readBytes;
    try {
      while ((readBytes = reader.read(buffer)) != -1) {
        final String s = new String(buffer, 0, readBytes);
        textArea.append(s);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
