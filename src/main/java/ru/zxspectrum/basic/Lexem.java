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
    @Getter
    @NonNull
    protected LexemType type;

    @Getter
    @NonNull
    protected String value;

    @Getter
    protected Integer intValue;

    public Lexem() {

    }

    public Lexem(@NonNull LexemType type, @NonNull Integer intValue) {
        if (type != LexemType.Number) {
            throw new IllegalArgumentException("Bad type: " + type);
        }
        this.setType(type);
        this.setIntValue(intValue);
    }

    public void setType(@NonNull LexemType type) {
        if (this.type == type) {
            return;
        }
        this.type = type;
        if (type == LexemType.Number) {
            value = null;
            intValue = null;
        }
    }

    public void setIntValue(@NonNull Integer intValue) {
        this.intValue = intValue;
        this.value = String.valueOf(intValue);
    }

    public void setValue(@NonNull String value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;
        if (type == LexemType.Number) {
            intValue = Integer.valueOf(value);
        }
    }

    @Override
    public String toString() {
        return value + ((intValue != null) ? "[" + intValue + "]" : "");
    }
}
