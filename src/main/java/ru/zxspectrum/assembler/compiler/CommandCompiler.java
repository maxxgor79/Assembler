package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.syntax.LexemSequence;

public interface CommandCompiler {
    default byte[] compile(LexemSequence lexemSequence) {
        return compile(lexemSequence, true);
    }

    public byte[] compile(LexemSequence lexemSequence, boolean ignoreLabel);
}
