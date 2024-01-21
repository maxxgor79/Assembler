package ru.assembler.core.error;

import ru.assembler.core.error.text.MessageFormatter;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class AssemblerException extends RuntimeException {

    private AssemblerException() {

    }

    public AssemblerException(File file, int lineNumber, String text, String... args) {
        super(MessageFormatter.generateErrorText(file, lineNumber, text, args));
    }

    public AssemblerException(String text) {
        super(text);
    }

    public AssemblerException(String text, Throwable cause) {
        super(text, cause);
    }
}
