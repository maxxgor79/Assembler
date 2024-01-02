package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.Expression;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class OrgCommandCompiler implements CommandCompiler {
    public static final String NAME = "org";

    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

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
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_EXCEPTED));
        }
        nextLexem = iterator.next();
        Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
        Expression.Result result = expression.evaluate(nextLexem);
        if (result.isUndefined()) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber()
                    , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
        }
        if (!TypeUtil.isInRange(settingsApi.getMinAddress(), settingsApi.getMaxAddress(), result.getValue())) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), String.valueOf(result.getValue()));
        }
        nextLexem = expression.getLastLexem();
        if (nextLexem != null) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        namespaceApi.setAddress(result.getValue());
        return new byte[0];
    }
}
