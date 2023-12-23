package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class SyntaxException extends AssemblerException {
    public SyntaxException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
