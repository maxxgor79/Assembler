package ru.zxspectrum.assembler.error;

import ru.zxspectrum.assembler.error.text.MessageFormatter;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class AssemblerException extends RuntimeException {
    private String message;

    private AssemblerException() {

    }

    public AssemblerException(File file, int lineNumber, String text, String... args) {
        super(MessageFormatter.generateErrorText(file, lineNumber, text, args));
    }

    public AssemblerException(String text) {
        super(text);
    }
}
