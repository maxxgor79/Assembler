package ru.zxspectrum.assembler.compiler.command.system;

import ru.zxspectrum.assembler.NamespaceApi;
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
public class DefCommandCompiler implements CommandCompiler {
    public static final String NAME = "def";

    public static final String ALT_NAME = "define";

    private String name;

    private NamespaceApi namespaceApi;

    private CompilerApi compilerApi;

    public DefCommandCompiler(String name, NamespaceApi namespaceApi, CompilerApi compilerApi) {
        if (name == null || name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        if (namespaceApi == null) {
            throw new NullPointerException("namespaceApi");
        }
        this.namespaceApi = namespaceApi;
        if (compilerApi == null) {
            throw new NullPointerException("compilerApi");
        }
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(LexemSequence lexemSequence, boolean ignoreLabel) {
        if (lexemSequence == null) {
            return null;
        }
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
