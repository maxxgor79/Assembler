package ru.assembler.microsha.core.compiler.command.system;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.option.Option;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.microsha.core.compiler.option.OptionTypes;

/**
 * @author Maxim Gorin
 */

public class WavCommandCompiler implements CommandCompiler {
    protected static final String[] NAMES = {"saveWav"};

    private final CompilerApi compilerApi;

    public WavCommandCompiler(@NonNull CompilerApi compilerApi) {
        this.compilerApi = compilerApi;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        Iterator<Lexem> iterator = lexemSequence.get().iterator();
        Lexem nextLexem;
        if (!iterator.hasNext() || !contains(names(), (nextLexem = iterator.next()).getValue())) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        if (nextLexem == null) {
            throw new CompilerException(compilerApi.getFd(), compilerApi.getLineNumber(), Messages
                    .getMessage(Messages.FILE_PATH_EXCEPTED));
        }
        final List<String> paths = new LinkedList<>();
        while (true) {
            if (nextLexem.getType() == LexemType.STRING) {
                final String path = nextLexem.getValue();
                paths.add(path);
                nextLexem = iterator.hasNext() ? iterator.next() : null;
            } else {
                throw new CompilerException(compilerApi.getFd(), nextLexem.getLineNumber(), Messages
                        .getMessage(Messages.UNEXPECTED_SYMBOL), nextLexem.getValue());
            }
            if (nextLexem == null) {
                break;
            }
        }
        compilerApi.addOption(new Option(OptionTypes.PRODUCE_WAV, paths));
        return new byte[0];
    }
}
