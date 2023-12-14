package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.syntax.Expression;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.RepeatableIterator;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
@Slf4j
public class DefCommandCompiler implements CommandCompiler {
    public static final String NAME = "def";

    public static final String ALT_NAME = "define";

    private String name;

    private NamespaceApi namespaceApi;

    private CompilerApi compilerApi;

    public DefCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi
            , @NonNull CompilerApi compilerApi) {
        if (name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence, boolean ignoreLabel) {
        RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<Lexem>(lexemSequence.get().iterator());
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
        String name = nextLexem.getValue();
        if (!iterator.hasNext()) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.VALUE_EXCEPTED));
        }
        Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
        nextLexem = iterator.next();
        BigInteger value = expression.evaluate(nextLexem);
        if (expression.getLastLexem() != null) {
            nextLexem = expression.getLastLexem();
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());

        }
        if (namespaceApi.containsVariable(name)) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList.
                    getMessage(MessageList.VARIABLE_IS_ALREADY_DEFINED), name);
        }
        namespaceApi.addVariable(name, value);
        return new byte[0];
    }
}
