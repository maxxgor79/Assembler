package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DefCommandCompiler implements CommandCompiler {
    public static final String NAME = "def";

    public static final String ALT_NAME = "define";

    private final String name;

    private final NamespaceApi namespaceApi;

    private final CompilerApi compilerApi;

    public DefCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi
            , @NonNull CompilerApi compilerApi) {
        if (name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    public DefCommandCompiler(NamespaceApi namespaceApi, CompilerApi compilerApi) {
        this(NAME, namespaceApi, compilerApi);
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(lexemSequence.get().iterator());
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (name.compareToIgnoreCase((nextLexem = iterator.next()).getValue()) != 0)) {
            return null;
        }
        if (!iterator.hasNext()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.IDENTIFIER_EXPECTED));
        }
        nextLexem = iterator.next();
        if (nextLexem.getType() != LexemType.IDENTIFIER) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.IDENTIFIER_EXPECTED_FOUND), nextLexem.getType().getName());
        }
        String name = nextLexem.getValue();
        if (!iterator.hasNext()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.VALUE_EXCEPTED));
        }
        final Expression expression = new Expression(nextLexem.getFile(), iterator, namespaceApi);
        nextLexem = iterator.next();
        final Expression.Result result = expression.evaluate(nextLexem);
        if (result.isUndefined()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
        }
        if (expression.getLastLexem() != null) {
            nextLexem = expression.getLastLexem();
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());

        }
        if (namespaceApi.containsVariable(name)) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList.
                    getMessage(MessageList.VARIABLE_IS_ALREADY_DEFINED), name);
        }
        namespaceApi.addVariable(name, result.getValue());
        return new byte[0];
    }
}
