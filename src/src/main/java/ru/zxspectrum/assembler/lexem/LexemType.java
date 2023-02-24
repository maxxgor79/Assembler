package ru.zxspectrum.assembler.lexem;

/**
 * @Author Maxim Gorin
 */
public enum LexemType {
    IDENTIFIER,
    BINARY,
    OCTAL,
    DECIMAL,
    HEXADECIMAL,
    COMMENT,
    PLUS,
    MINUS,
    SLASH,
    STAR,
    COMMA,
    OPEN_BRACE,
    CLOSED_BRACE,
    EOL,
    EOS,
    LABEL,
    CHAR,
    STRING,
    VARIABLE,
    AMPERSAND,
    PERCENT,
    PIPE,
    TILDE,
    LSHIFT,
    RSHIFT,
    CARET,
    DOT,
    HASH,
    UNKNOWN;
}
