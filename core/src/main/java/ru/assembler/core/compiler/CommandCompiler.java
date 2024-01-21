package ru.assembler.core.compiler;

import ru.assembler.core.syntax.LexemSequence;

/**
 * @author Maxim Gorin
 */
public interface CommandCompiler {
    byte[] compile(LexemSequence lexemSequence);
}
