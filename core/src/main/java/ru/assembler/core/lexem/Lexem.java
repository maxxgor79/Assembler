package ru.assembler.core.lexem;

import java.io.File;
import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.util.Converter;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
@Getter
public class Lexem {

  @Getter
  private File file;

  @Getter
  private int lineNumber;

  @Getter
  private LexemType type;

  @Getter
  private String value;

  private Lexem() {

  }

  public Lexem(@NonNull LexemType type, @NonNull String value) {
    this.type = type;
    this.value = value;
  }

  public Lexem(File file, int lineNumber, LexemType type) {
    this(file, lineNumber, type, null);
  }

  public Lexem(File file, int lineNumber, @NonNull LexemType type, String value) {
    if (lineNumber < 0) {
      throw new IllegalArgumentException("lineNumber is negative");
    }
    this.file = file;
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
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
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
