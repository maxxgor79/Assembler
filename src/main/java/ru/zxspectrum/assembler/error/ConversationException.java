package ru.zxspectrum.assembler.error;

public class ConversationException extends RuntimeException {
    public ConversationException() {
    }

    public ConversationException(String message) {
        super(message);
    }

    public ConversationException(String message, Throwable cause) {
        super(message, cause);
    }
}
