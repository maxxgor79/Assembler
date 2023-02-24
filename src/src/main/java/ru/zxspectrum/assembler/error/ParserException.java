package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public class ParserException extends AssemblerException {
    public ParserException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }

    public ParserException(String text) {
        super(text);
    }
}
