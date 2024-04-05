package ru.assembler.core.lexem;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.error.text.Messages;

/**
 * @author Maxim Gorin
 */
@Getter
public enum LexemType {
    IDENTIFIER(Messages.getMessage(Messages.IDENTIFIER)),
    BINARY(Messages.getMessage(Messages.NUMBER)),
    OCTAL(Messages.getMessage(Messages.NUMBER)),
    DECIMAL(Messages.getMessage(Messages.NUMBER)),
    HEXADECIMAL(Messages.getMessage(Messages.NUMBER)),
    COMMENT(Messages.getMessage(Messages.COMMENT)),
    PLUS(Messages.getMessage(Messages.DELIMITER)),
    MINUS(Messages.getMessage(Messages.DELIMITER)),
    SLASH(Messages.getMessage(Messages.DELIMITER)),
    STAR(Messages.getMessage(Messages.DELIMITER)),
    COMMA(Messages.getMessage(Messages.DELIMITER)),
    OPEN_BRACE(Messages.getMessage(Messages.DELIMITER)),
    CLOSED_BRACE(Messages.getMessage(Messages.DELIMITER)),
    EOL(Messages.getMessage(Messages.EOL)),
    EOF("EOF"),
    LABEL(Messages.getMessage(Messages.LABEL)),
    CHAR(Messages.getMessage(Messages.CHAR)),
    STRING(Messages.getMessage(Messages.STRING)),
    VARIABLE(Messages.getMessage(Messages.VARIABLE)),
    AMPERSAND(Messages.getMessage(Messages.DELIMITER)),
    PERCENT(Messages.getMessage(Messages.DELIMITER)),
    PIPE(Messages.getMessage(Messages.DELIMITER)),
    TILDE(Messages.getMessage(Messages.DELIMITER)),
    LSHIFT(Messages.getMessage(Messages.DELIMITER)),
    RSHIFT(Messages.getMessage(Messages.DELIMITER)),
    CARET(Messages.getMessage(Messages.DELIMITER)),
    DOT(Messages.getMessage(Messages.DELIMITER)),
    HASH(Messages.getMessage(Messages.DELIMITER)),
    UNKNOWN("?");

    LexemType(@NonNull String name) {
        this.name = name;
    }

    private final String name;
}
