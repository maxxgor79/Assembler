package ru.zxspectrum.disassembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 */
public class RenderException extends Exception {
    public RenderException() {
    }

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }
}
