package ru.assembler.core.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class CompilerException extends AssemblerException {
    public CompilerException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }

    public CompilerException(String text) {
        super(text);
    }

    public CompilerException(String text, Throwable cause) {
        super(text, cause);
    }
}
