package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.RepeatableIterator;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

/**
 * @author Maxim Gorin
 */
public class UdefCommandCompiler implements CommandCompiler {
    public static final String NAME = "udef";

    public static final String ALT_NAME = "undefine";

    private final String name;

    private final NamespaceApi namespaceApi;

    private final CompilerApi compilerApi;

    public UdefCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi
            , @NonNull CompilerApi compilerApi) {
        if (name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(LexemSequence lexemSequence) {
        RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(lexemSequence.get().iterator());
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (name.compareToIgnoreCase((nextLexem = iterator.next()).getValue()) != 0)) {
            return null;
        }
        if (!iterator.hasNext()) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
        nextLexem = iterator.next();
        if (nextLexem.getType() != LexemType.IDENTIFIER) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.IDENTIFIER_EXPECTED_FOUND), nextLexem.getValue());
        }
        final String name = nextLexem.getValue();
        namespaceApi.removeVariable(name);
        return new byte[0];
    }
}
