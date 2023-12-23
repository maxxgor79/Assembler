package ru.zxspectrum.assembler.lexem;

import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.assembler.util.Converter;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
public class Lexem {
    @Getter
    private int lineNumber;

    @Getter
    private LexemType type;

    @Getter
    private String value;

    private Lexem() {

    }

    public Lexem(int lineNumber, @NonNull LexemType type) {
        this(lineNumber, type, null);
    }

    public Lexem(int lineNumber, @NonNull LexemType type, String value) {
        this.lineNumber = lineNumber;
        this.type = type;
        this.value = value;
    }

    private BigInteger toDecimal(String value, LexemType type) {
        return switch (type) {
            case HEXADECIMAL -> Converter.hexadecimalToBiginteger(value);
            case DECIMAL -> Converter.decimalToBigInteger(value);
            case OCTAL -> Converter.octalToBigInteger(value);
            case BINARY -> Converter.binaryToBigInteger(value);
            default -> null;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Lexem other = (Lexem) o;
        if (type != other.type) {
            return false;
        }
        switch (type) {
            case VARIABLE -> {
                return true;
            }
            case HEXADECIMAL, OCTAL, BINARY, DECIMAL -> {
                if (toDecimal(value, type).equals(toDecimal(other.value, other.type))) {
                    return true;
                }
            }
            default -> {
                return value.compareToIgnoreCase(other.value) == 0;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value.toLowerCase());
    }

    @Override
    public String toString() {
        return "Lexem{" +
                "lineNumber=" + lineNumber +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
