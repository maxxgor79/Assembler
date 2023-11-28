package ru.zxspectrum.assembler.lexem;

import lombok.NonNull;
import ru.zxspectrum.assembler.util.Converter;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @Author Maxim Gorin
 */
public class Lexem {
    private int lineNumber;

    private LexemType type;

    private String value;

    private Lexem() {

    }

    public Lexem(int lineNumber, LexemType type) {
        this(lineNumber, type, null);
    }

    public Lexem(int lineNumber, @NonNull LexemType type, String value) {
        this.lineNumber = lineNumber;
        this.type = type;
        this.value = value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public LexemType getType() {
        return type;
    }

    public String getValue() {
        return value;
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
        Lexem lexem = (Lexem) o;
        BigInteger n1, n2;
        n1 = toDecimal(value, type);
        n2 = toDecimal(lexem.value, lexem.type);
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }
        if (n1 == null && n2 == null) {
            return type == lexem.type && value.compareToIgnoreCase(lexem.value) == 0;
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
