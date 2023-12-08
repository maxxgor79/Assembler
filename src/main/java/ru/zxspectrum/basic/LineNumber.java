package ru.zxspectrum.basic;

import lombok.NonNull;
import ru.zxspectrum.basic.Lexem;

public class LineNumber extends Lexem {
    public LineNumber() {
        super();

    }

    public LineNumber(@NonNull String number) {
        super();
        setValue(number);
    }

    public LineNumber(int lineNumber) {
        setIntValue(lineNumber);
    }

    @Override
    public void setValue(@NonNull String value) {
        this.value = value;
        this.intValue = Integer.valueOf(value);
    }

    public void setIntValue(int value) {
        this.intValue = value;
        this.value = String.valueOf(intValue);
    }

    @Override
    public String toString() {
        return value;
    }
}
