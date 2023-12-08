package ru.zxspectrum.basic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class Lexem {
    @Setter
    @Getter
    @NonNull
    protected LexemType type;

    @Setter
    @Getter
    @NonNull
    protected String value;

    @Setter
    @Getter
    protected Integer intValue;

    public Lexem() {

    }

    @Override
    public String toString() {
        return value + ((intValue != null) ? "[" + intValue + "]" : "");
    }
}
