package ru.retro.assembler.editor.core.io;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2024
 */
@Slf4j
public class ConsoleWriter implements Runnable {
    private final Process process;

    private final JTextArea textArea;

    public ConsoleWriter(@NonNull final Process process, @NonNull final JTextArea textArea) {
        this.process = process;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        final InputStream is = process.getInputStream();
        final byte[] buffer = new byte[4096];
        int readBytes;
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                String s = new String(buffer, 0, readBytes);
                textArea.append(s);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
