package ru.zxspectrum.disassembler.lexem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author maxim
 * Date: 12/25/2023
 */
@EqualsAndHashCode
public class Lexem {
    @Getter
    @NonNull
    @Setter(AccessLevel.PROTECTED)
    private LexemType type;

    @Getter
    @NonNull
    @Setter(AccessLevel.PROTECTED)
    private String value;

    public Lexem(@NonNull LexemType type, @NonNull String value) {
        setType(type);
        setValue(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
