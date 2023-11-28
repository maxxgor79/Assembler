package ru.zxspectrum.assembler.compiler.command.parameterized;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CommandGroupCompiler;
import ru.zxspectrum.assembler.syntax.LexemSequence;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Maxim Gorin
 */
public class ParameterizedCommandGroupCompiler implements CommandGroupCompiler {
    private Set<CommandCompiler> compilers = new HashSet<>();

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence, boolean ignoreLabel) {
        for (CommandCompiler compiler : compilers) {
            byte[] byteCode = compiler.compile(lexemSequence, ignoreLabel);
            if (byteCode != null) {
                return byteCode;
            }
        }
        return null;
    }

    @Override
    public boolean add(CommandCompiler compiler) {
        if (compiler == null) {
            return false;
        }
        return compilers.add(compiler);
    }

    public void clear() {
        compilers.clear();
    }

    public boolean remove(@NonNull CommandCompiler compiler) {
        return compilers.remove(compiler);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CommandCompiler compiler : compilers) {
            sb.append(compilers.toString()).append(" ");
        }
        return sb.toString();
    }
}
