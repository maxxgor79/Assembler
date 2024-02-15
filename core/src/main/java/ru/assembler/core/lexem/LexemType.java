package ru.assembler.core.lexem;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.error.text.MessageList;

/**
 * @author Maxim Gorin
 */
@Getter
public enum LexemType {
    IDENTIFIER(MessageList.getMessage(MessageList.IDENTIFIER)),
    BINARY(MessageList.getMessage(MessageList.NUMBER)),
    OCTAL(MessageList.getMessage(MessageList.NUMBER)),
    DECIMAL(MessageList.getMessage(MessageList.NUMBER)),
    HEXADECIMAL(MessageList.getMessage(MessageList.NUMBER)),
    COMMENT(MessageList.getMessage(MessageList.COMMENT)),
    PLUS(MessageList.getMessage(MessageList.DELIMITER)),
    MINUS(MessageList.getMessage(MessageList.DELIMITER)),
    SLASH(MessageList.getMessage(MessageList.DELIMITER)),
    STAR(MessageList.getMessage(MessageList.DELIMITER)),
    COMMA(MessageList.getMessage(MessageList.DELIMITER)),
    OPEN_BRACE(MessageList.getMessage(MessageList.DELIMITER)),
    CLOSED_BRACE(MessageList.getMessage(MessageList.DELIMITER)),
    EOL(MessageList.getMessage(MessageList.EOL)),
    EOF("EOF"),
    LABEL(MessageList.getMessage(MessageList.LABEL)),
    CHAR(MessageList.getMessage(MessageList.CHAR)),
    STRING(MessageList.getMessage(MessageList.STRING)),
    VARIABLE(MessageList.getMessage(MessageList.VARIABLE)),
    AMPERSAND(MessageList.getMessage(MessageList.DELIMITER)),
    PERCENT(MessageList.getMessage(MessageList.DELIMITER)),
    PIPE(MessageList.getMessage(MessageList.DELIMITER)),
    TILDE(MessageList.getMessage(MessageList.DELIMITER)),
    LSHIFT(MessageList.getMessage(MessageList.DELIMITER)),
    RSHIFT(MessageList.getMessage(MessageList.DELIMITER)),
    CARET(MessageList.getMessage(MessageList.DELIMITER)),
    DOT(MessageList.getMessage(MessageList.DELIMITER)),
    HASH(MessageList.getMessage(MessageList.DELIMITER)),
    UNKNOWN("?");

    LexemType(@NonNull String name) {
        this.name = name;
    }

    private final String name;
}
