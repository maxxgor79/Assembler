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
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;

import java.math.BigInteger;

@Slf4j
public class EquCommandCompiler implements CommandCompiler {
    public static final String NAME = "equ";

    private final NamespaceApi namespaceApi;

    private final CompilerApi compilerApi;

    public EquCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        final RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(lexemSequence.get().iterator());
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (NAME.compareToIgnoreCase((nextLexem = iterator.next()).getValue()) != 0)) {
            return null;
        }
        if (iterator.hasNext()) {
            nextLexem = iterator.next();
        } else {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_EXCEPTED));
        }
        final Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
        final Expression.Result result = expression.evaluate(nextLexem);
        if (result.isUndefined()) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
        }
        final String labelName = namespaceApi.getLabel(namespaceApi.getCurrentCodeOffset());
        if (labelName == null) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.LABEL_DECLARATION_REQUIRED));
        }
        //transform absolute address to relative
        namespaceApi.putLabel(labelName, result.getValue().subtract(namespaceApi.getAddress()));
        nextLexem = expression.getLastLexem();
        if (nextLexem != null) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        return new byte[0];
    }
}
