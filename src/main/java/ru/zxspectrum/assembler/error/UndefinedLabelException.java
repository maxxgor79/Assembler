package ru.zxspectrum.assembler.error;

public class UndefinedLabelException extends RuntimeException {
    public UndefinedLabelException() {
    }

    public UndefinedLabelException(String message) {
        super(message);
    }
}
