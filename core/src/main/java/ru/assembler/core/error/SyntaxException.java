package ru.assembler.core.error;

import ru.assembler.core.io.FileDescriptor;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class SyntaxException extends AssemblerException {
    public SyntaxException(FileDescriptor fd, int lineNumber, String text, String... args) {
        super(fd, lineNumber, text, args);
    }
}
