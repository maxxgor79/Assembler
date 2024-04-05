package ru.assembler.zxspectrum.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.syntax.LexemSequence;

import java.util.Iterator;

public class Z80CommandCompiler implements CommandCompiler {
    protected static final String[] NAMES = {"z80"};

    private final CompilerApi compilerApi;

    public Z80CommandCompiler(@NonNull final CompilerApi compilerApi) {
        this.compilerApi = compilerApi;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public byte[] compile(LexemSequence lexemSequence) {
        Iterator<Lexem> iterator = lexemSequence.get().iterator();
        Lexem nextLexem;
        if (!iterator.hasNext() || !contains(NAMES, (nextLexem = iterator.next()).getValue())) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem != null) {
            throw new CompilerException(compilerApi.getFd(), compilerApi.getLineNumber(), Messages
                    .getMessage(Messages.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        return new byte[0];
    }
}
