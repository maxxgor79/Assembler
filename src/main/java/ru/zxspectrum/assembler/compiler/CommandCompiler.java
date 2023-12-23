package ru.zxspectrum.assembler.compiler;

import ru.zxspectrum.assembler.syntax.LexemSequence;

/**
 * @author Maxim Gorin
 */
public interface CommandCompiler {
    byte[] compile(LexemSequence lexemSequence);
}
