package ru.zxspectrum.basic.decompile;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
