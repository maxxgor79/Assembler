package ru.zxspectrum.assembler.compiler.command.noparameterized;

import lombok.NonNull;
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
    public byte[] compile(@NonNull LexemSequence lexemSequence, boolean ignoreLabel) {
        return compiler.compile(lexemSequence, ignoreLabel);
    }

    @Override
    public boolean add(@NonNull CommandCompiler compiler) {
        this.compiler = compiler;
        return true;
    }
}
