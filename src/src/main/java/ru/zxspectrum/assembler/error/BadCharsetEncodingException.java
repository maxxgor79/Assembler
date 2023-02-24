package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public class BadCharsetEncodingException extends AssemblerException {
    public BadCharsetEncodingException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
