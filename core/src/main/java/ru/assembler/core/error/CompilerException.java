package ru.assembler.core.error;

import ru.assembler.core.io.FileDescriptor;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class CompilerException extends AssemblerException {
    public CompilerException(FileDescriptor fd, int lineNumber, String text, String... args) {
        super(fd, lineNumber, text, args);
    }

    public CompilerException(String text) {
        super(text);
    }

    public CompilerException(String text, Throwable cause) {
        super(text, cause);
    }
}
