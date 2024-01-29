package ru.assembler.core.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */

public class LexemException extends AssemblerException {
    public LexemException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
