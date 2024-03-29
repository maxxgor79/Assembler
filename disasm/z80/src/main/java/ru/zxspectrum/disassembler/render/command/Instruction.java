package ru.zxspectrum.disassembler.render.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.bytecode.ByteCodeType;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnit;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnits;
import ru.zxspectrum.disassembler.bytecode.Pattern;
import ru.zxspectrum.disassembler.command.Behavior;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.io.LEDataOutputStream;
import ru.zxspectrum.disassembler.lexem.Lexem;
import ru.zxspectrum.disassembler.lexem.LexemType;
import ru.zxspectrum.disassembler.lexem.Lexemes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author maxim Date: 12/27/2023
 */
@Slf4j
@EqualsAndHashCode
public class Instruction extends Command {

  private final List<Variable> variables = new ArrayList<>();

  @Getter
  private final Lexemes lexemes;

  @Getter
  private final ByteCodeUnits units;

  @Getter
  private final Behavior behavior;

  @Getter
  private final String jumpAddressPattern;

  private byte[] byteCode;

  public Instruction(@NonNull ByteCodeUnits units, @NonNull Lexemes lexemes,
      @NonNull Behavior behavior
      , String jumpAddressPattern) {
    this.units = units;
    this.lexemes = lexemes;
    this.behavior = behavior;
    this.jumpAddressPattern = jumpAddressPattern;
  }

  public Instruction(ByteCodeUnits units, Lexemes lexemes, Behavior behavior,
      String jumpAddressPattern
      , Collection<Variable> variables) {
    this(units, lexemes, behavior, jumpAddressPattern);
    setVariables(variables);
  }

  @Override
  public String generate() throws RenderException {
    int variableIndex = 0;
    Lexem prevLexem = null;
    final StringBuilder sb = new StringBuilder();
    for (Lexem lexem : lexemes.toCollection()) {
      Formatter.format(sb, prevLexem, lexem);
      if (lexem.getType() == LexemType.Variable) {
        if (variableIndex < variables.size()) {
          sb.append(variables.get(variableIndex++).generate());
        }
      }
      prevLexem = lexem;
    }
    return toUpperOrLowerCase(sb.toString());
  }

  public void setVariables(@NonNull Collection<Variable> variables) {
    this.variables.clear();
    this.variables.addAll(variables);
  }

  public void setVariable(int index, @NonNull Variable var) {
    variables.set(index, var);
  }

  public Collection<Variable> getVariables() {
    return Collections.unmodifiableCollection(variables);
  }

  public int getVariableCount() {
    return variables.size();
  }

  public void setLexemes(@NonNull Collection<Lexem> lexemes) {
    this.lexemes.clear();
    this.lexemes.addAll(lexemes);
  }

  public Variable getVariable(int index) {
    return variables.get(index);
  }

  public int getByteCodeSize(int i) {
    return units.getOffsetInBytes(i);
  }

  @Override
  public byte[] toByteCode() {
    if (byteCode != null) {
      return byteCode;
    }
    int varIndex = 0;
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final LEDataOutputStream leDos = new LEDataOutputStream(baos);
    try {
      for (ByteCodeUnit bcu : units.toCollection()) {
        if (bcu.getType() == ByteCodeType.Code) {
          leDos.writeByte(Integer.parseInt(bcu.getValue(), 16));
        } else if (bcu.getType() == ByteCodeType.Pattern) {
          final Pattern pattern = new Pattern(bcu.getValue());
          switch (pattern.getDimension()) {
            case 1:
              leDos.writeByte(variables.get(varIndex++).getValue().byteValue());
              break;
            case 2:
              leDos.writeShort(variables.get(varIndex++).getValue().shortValue());
              break;
            case 4:
              leDos.writeInt(variables.get(varIndex++).getValue().intValue());
              break;
            case 8:
              leDos.writeLong(variables.get(varIndex++).getValue().longValue());
              break;
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
    return byteCode = baos.toByteArray();
  }
}
