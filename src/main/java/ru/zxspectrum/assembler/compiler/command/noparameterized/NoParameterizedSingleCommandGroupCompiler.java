package ru.zxspectrum.assembler.compiler.command.noparameterized;

import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CommandGroupCompiler;
import ru.zxspectrum.assembler.syntax.LexemSequence;

/**
 * @Author Maxim Gorin
 */
public class NoParameterizedSingleCommandGroupCompiler implements CommandGroupCompiler {
    private CommandCompiler compiler;

    public NoParameterizedSingleCommandGroupCompiler(CommandCompiler compiler) {
        add(compiler);
    }

    @Override
    public byte[] compile(LexemSequence lexemSequence, boolean ignoreLabel) {
        return compiler.compile(lexemSequence, ignoreLabel);
    }

    @Override
    public boolean add(CommandCompiler compiler) {
        if (compiler == null) {
            throw new IllegalArgumentException("compiler is null");
        }
        this.compiler = compiler;
        return true;
    }
}
