package ru.retro.assembler.editor.core.io;

import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class ConsoleOutputStream extends OutputStream {
    @Setter
    private Appender appender;

    private final StringBuilder line = new StringBuilder();

    public ConsoleOutputStream(Appender appender) {
        this.appender = appender;
    }

    @Override
    public void write(int b) throws IOException {
        switch (b) {
            case '\r':
                break;
            case '\n':
                line.append((char) b);
                if (appender != null) {
                    appender.addLine(line.toString());
                }
                line.setLength(0);
                break;
            default:
                line.append((char) b);
                break;
        }
    }

    public interface Appender {
        void addLine(String s);
    }
}
