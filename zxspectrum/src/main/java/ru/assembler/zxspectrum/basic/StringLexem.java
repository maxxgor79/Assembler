package ru.assembler.zxspectrum.basic;

import lombok.NonNull;

/**
 * @author Maxim Gorin
 */
public class StringLexem extends Lexem {
    public StringLexem(@NonNull LexemType type, @NonNull String value, Integer intValue) {
        super(type, value, intValue);
    }

    public StringLexem(@NonNull LexemType type, @NonNull String value) {
        super(type, value);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
