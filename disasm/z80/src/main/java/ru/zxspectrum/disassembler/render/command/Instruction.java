package ru.zxspectrum.disassembler.render.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.disassembler.bytecode.ByteCodeUnits;
import ru.zxspectrum.disassembler.command.Behavior;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.lexem.Lexem;
import ru.zxspectrum.disassembler.lexem.LexemType;
import ru.zxspectrum.disassembler.lexem.Lexemes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author maxim
 * Date: 12/27/2023
 */
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

    public Instruction(@NonNull ByteCodeUnits units, @NonNull Lexemes lexemes, @NonNull Behavior behavior
            , String jumpAddressPattern) {
        this.units = units;
        this.lexemes = lexemes;
        this.behavior = behavior;
        this.jumpAddressPattern = jumpAddressPattern;
    }

    public Instruction(ByteCodeUnits units, Lexemes lexemes, Behavior behavior, String jumpAddressPattern
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
}
