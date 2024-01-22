package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;

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
            throw new IllegalArgumentException("name is empty");
        }
        this.name = name;
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    public UdefCommandCompiler(@NonNull NamespaceApi namespaceApi
            , @NonNull CompilerApi compilerApi) {
        this(NAME, namespaceApi, compilerApi);
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
        nextLexem = iterator.next();
        if (nextLexem != null) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        return new byte[0];
    }
}
