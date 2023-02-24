package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public class LexemException extends AssemblerException {
    public LexemException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
