package ru.zxspectrum.assembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class InvalidFormatNumberException extends LexemException {

    public InvalidFormatNumberException(File file, int lineNumber, String text, String... args) {
        super(file, lineNumber, text, args);
    }
}
