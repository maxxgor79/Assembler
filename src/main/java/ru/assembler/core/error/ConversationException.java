package ru.assembler.core.error;

/**
 * @author Maxim Gorin
 */
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
