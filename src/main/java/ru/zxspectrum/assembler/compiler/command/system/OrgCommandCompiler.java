package ru.zxspectrum.assembler.compiler.command.system;

import ru.zxspectrum.assembler.NamespaceApi;
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
 * @Author Maxim Gorin
 */
public class OrgCommandCompiler implements CommandCompiler {
    public static final String NAME = "org";

    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

    public OrgCommandCompiler(NamespaceApi namespaceApi, SettingsApi settingsApi, CompilerApi compilerApi) {
        if (namespaceApi == null) {
            throw new NullPointerException("namespaceApi");
        }
        this.namespaceApi = namespaceApi;
        if (settingsApi == null) {
            throw new NullPointerException("settingsApi");
        }
        this.settingsApi = settingsApi;
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
        BigInteger address = expression.evaluate(nextLexem);
        nextLexem = expression.getLastLexem();
        if (nextLexem != null) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
        }
        if (!TypeUtil.isInRange(settingsApi.getMinAddress(), settingsApi.getMaxAddress(), address)) {
            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), String.valueOf(address));
        }
        namespaceApi.setAddress(address);
        return new byte[0];
    }
}
