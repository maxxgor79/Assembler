package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @Author Maxim Gorin
 */
public class ExpressionException extends AssemblerException {
    public ExpressionException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
