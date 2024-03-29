package ru.zxspectrum.disassembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public class ParserException extends DisassemblerException {
    public ParserException() {
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(int pos, String message, Object... args) {
        super(pos, message, args);
    }

    public ParserException(File file, int pos, String message, Object... args) {
        super(file, pos, message, args);
    }
}
