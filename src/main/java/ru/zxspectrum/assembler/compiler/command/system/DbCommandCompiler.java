package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.ConversationException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Type;
import ru.zxspectrum.assembler.lang.TypeConverter;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.Expression;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.IOUtils;
import ru.zxspectrum.assembler.util.RepeatableIterator;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DbCommandCompiler implements CommandCompiler {
    public static final String NAME = "db";

    public static final String ALT_NAME = "defb";

    private final String name;

    protected CompilerApi compilerApi;

    protected NamespaceApi namespaceApi;

    protected SettingsApi settingsApi;

    public DbCommandCompiler(@NonNull String name, @NonNull NamespaceApi namespaceApi
            , @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi) {
        if (name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.compilerApi = compilerApi;
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<>(lexemSequence.get().iterator());
            Lexem nextLexem;
            if (!iterator.hasNext() ||
                    (name.compareToIgnoreCase((iterator.next()).getValue())) != 0) {
                return null;
            }
            nextLexem = iterator.hasNext() ? iterator.next() : null;
            while (true) {
                if (nextLexem == null) {
                    throw new CompilerException(compilerApi.getFile(), compilerApi.getLineNumber(), MessageList
                            .getMessage(MessageList.VALUE_EXCEPTED));
                }
                if (nextLexem.getType() == LexemType.STRING) {
                    IOUtils.writeString(baos, nextLexem.getValue());
                    nextLexem = iterator.hasNext() ? iterator.next() : null;
                } else {
                    if (nextLexem.getType() == LexemType.CHAR || nextLexem.getType() == LexemType.DECIMAL ||
                            nextLexem.getType() == LexemType.OCTAL || nextLexem.getType() == LexemType.HEXADECIMAL) {
                        final Expression expression = new Expression(compilerApi.getFile(), iterator, namespaceApi);
                        final Expression.Result result = expression.evaluate(nextLexem);
                        if (result.isUndefined()) {
                            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber()
                                    , MessageList.getMessage(MessageList.CONSTANT_VALUE_REQUIRED));
                        }
                        if (expression.getLastLexem() != null) {
                            nextLexem = expression.getLastLexem();
                        }
                        BigInteger value = result.getValue();
                        try {
                            final Type srcType = TypeUtil.typeOf(value);
                            value = TypeConverter.convert(srcType, value, Type.UInt8, settingsApi.isStrictConversion());
                        } catch (ConversationException e) {
                            log.error(e.getMessage(), e);
                            throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                                    .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.getValue().toString());
                        }
                        if (!result.getValue().equals(value)) {
                            Output.throwWarning(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                                            .getMessage(MessageList.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                                    , value.toString());
                        }
                        IOUtils.writeByte(baos, value.byteValue());
                        nextLexem = expression.getLastLexem();
                    } else {
                        throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                                .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
                    }
                }
                if (nextLexem == null) {
                    break;
                }
                if (nextLexem.getType() == LexemType.COMMA) {
                    nextLexem = iterator.hasNext() ? iterator.next() : null;
                } else {
                    throw new CompilerException(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.EXPECTED_SYMBOL), ",");
                }
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CompilerException(e.getMessage(), e);
        }
    }
}
