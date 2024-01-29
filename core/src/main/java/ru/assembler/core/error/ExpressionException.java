package ru.assembler.core.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */

public class ExpressionException extends AssemblerException {
    public ExpressionException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
