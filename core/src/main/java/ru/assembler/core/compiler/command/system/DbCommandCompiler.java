package ru.assembler.core.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.ConversationException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.error.text.Output;
import ru.assembler.core.lang.Type;
import ru.assembler.core.lang.TypeConverter;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.IOUtils;
import ru.assembler.core.util.RepeatableIterator;
import ru.assembler.core.util.RepeatableIteratorImpl;
import ru.assembler.core.util.TypeUtil;

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

    public DbCommandCompiler(NamespaceApi namespaceApi, SettingsApi settingsApi, CompilerApi compilerApi) {
        this(NAME, namespaceApi, settingsApi, compilerApi);
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
                        final Expression expression = new Expression(nextLexem.getFile(), iterator, namespaceApi);
                        final Expression.Result result = expression.evaluate(nextLexem);
                        if (result.isUndefined()) {
                            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber()
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
                            throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                                    .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.getValue().toString());
                        }
                        if (!result.getValue().equals(value)) {
                            Output.throwWarning(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                                            .getMessage(MessageList.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                                    , value.toString());
                        }
                        IOUtils.writeByte(baos, value.byteValue());
                        nextLexem = expression.getLastLexem();
                    } else {
                        throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
                                .getMessage(MessageList.UNEXPECTED_SYMBOL), nextLexem.getValue());
                    }
                }
                if (nextLexem == null) {
                    break;
                }
                if (nextLexem.getType() == LexemType.COMMA) {
                    nextLexem = iterator.hasNext() ? iterator.next() : null;
                } else {
                    throw new CompilerException(nextLexem.getFile(), nextLexem.getLineNumber(), MessageList
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
