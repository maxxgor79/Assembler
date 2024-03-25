package ru.zxspectrum.disassembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 * Date: 27.02.2023
 */
public class DecoderException extends DisassemblerException {
    public DecoderException() {
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoderException(Throwable cause) {
        super(cause);
    }

    public DecoderException(int pos, String message, Object... args) {
        super(pos, message, args);
    }

    public DecoderException(File file, int pos, String message, Object... args) {
        super(file, pos, message, args);
    }
}
