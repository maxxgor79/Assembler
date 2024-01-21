package ru.assembler.zxspectrum.basic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Maxim Gorin
 */
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Lexem {
    @NonNull
    protected LexemType type;

    @NonNull
    protected String value;

    protected Integer intValue;

    public Lexem() {
        setType(LexemType.Symbol);
    }

    public Lexem(@NonNull Integer intValue) {
        this.setIntValue(intValue);
    }

    public void setType(@NonNull LexemType type) {
        this.type = type;
        switch (type) {
            case Number -> this.intValue = (value == null) ? null : Integer.valueOf(value);
            case Symbol -> value = (value == null || value.isEmpty()) ? null : String.valueOf(value.charAt(0));
            case Eol -> value = System.lineSeparator();
            default -> intValue = null;
        }
    }

    public void setIntValue(@NonNull Integer intValue) {
        this.type = LexemType.Number;
        this.intValue = intValue;
        this.value = String.valueOf(intValue);
    }

    public void setValue(@NonNull String value) {
        this.value = value;
        switch (type) {
            case Number -> intValue = Integer.valueOf(value);
            case Symbol -> this.value = (value == null || value.isEmpty()) ? null
                    : String.valueOf(value.charAt(0));
            case Eol -> this.value = System.lineSeparator();
        }
    }

    @Override
    public String toString() {
        return value + ((intValue != null) ? "[" + intValue + "]" : "");
    }
}
