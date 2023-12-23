package ru.zxspectrum.assembler.compiler.command.system;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Type;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.syntax.Expression;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.RepeatableIterator;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DbCommandCompiler implements CommandCompiler {
    public static final String NAME = "db";

    public static final String ALT_NAME = "defb";

    private String name;

    private CompilerApi compilerApi;

    private NamespaceApi namespaceApi;

    public DbCommandCompiler(@NonNull String name, @NonNull CompilerApi compilerApi
            , @NonNull NamespaceApi namespaceApi) {
        if (name.trim().isEmpty()) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.compilerApi = compilerApi;
        this.namespaceApi = namespaceApi;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence lexemSequence) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RepeatableIterator<Lexem> iterator = new RepeatableIteratorImpl<Lexem>(lexemSequence.get().iterator());
        Lexem nextLexem;
        if (!iterator.hasNext() ||
                (name.compareToIgnoreCase((nextLexem = iterator.next()).getValue())) != 0) {
            return null;
        }
        nextLexem = iterator.hasNext() ? iterator.next() : null;
        while (true) {
            if (nextLexem == null) {
                throw new CompilerException(compilerApi.getFile(), compilerApi.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_EXCEPTED));
            }
            if (nextLexem.getType() == LexemType.STRING) {
                writeString(baos, nextLexem.getValue());
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
                    nextLexem = expression.getLastLexem();
                    if (!TypeUtil.isInRange(Type.Int8, result.getValue()) &&
                            !TypeUtil.isInRange(Type.UInt8, result.getValue())) {
                        Output.throwWarning(compilerApi.getFile(), nextLexem.getLineNumber(), MessageList
                                .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.toString(), Type.UInt8.toString());
                    }
                    writeByte(baos, result.getValue().byteValue());
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
    }

    private void writeString(OutputStream os, String value) {
        try {
            os.write(value.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeByte(OutputStream os, byte value) {
        try {
            os.write(value);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
