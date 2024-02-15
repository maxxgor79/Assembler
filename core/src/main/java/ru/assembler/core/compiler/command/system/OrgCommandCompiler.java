package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.TypeUtil;

import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class OrgCommandCompiler implements CommandCompiler {
    public static final String NAME = "org";

    private final NamespaceApi namespaceApi;

    private final SettingsApi settingsApi;

    private final CompilerApi compilerApi;

    public OrgCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull CompilerApi compilerApi) {
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.compilerApi = compilerApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        Iterator<Lexem> iterator = lexemSequence.get().iterator();
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (NAME.compareToIgnoreCase((nextLexem = iterator.next()).getValue()) != 0)) {
            return null;
        }
        if (!iterator.hasNext()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_EXCEPTED));
        }
        nextLexem = iterator.next();
        Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
        Expression.Result result = expression.evaluate(nextLexem);
        if (result.isUndefined()) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
        }
        if (!TypeUtil.isInRange(settingsApi.getMinAddress(), settingsApi.getMaxAddress(), result.getValue())) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), String.valueOf(result.getValue()));
        }
        nextLexem = expression.getLastLexem();
        if (nextLexem != null) {
            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        namespaceApi.setAddress(result.getValue());
        return new byte[0];
    }
}
