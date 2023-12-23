package ru.zxspectrum.basic;

import lombok.NonNull;
import ru.zxspectrum.basic.Lexem;
import ru.zxspectrum.basic.LexemType;

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
