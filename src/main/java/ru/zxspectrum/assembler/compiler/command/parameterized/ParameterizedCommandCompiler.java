package ru.zxspectrum.assembler.compiler.command.parameterized;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteCodeCompiler;
import ru.zxspectrum.assembler.compiler.dummy.CompilerApiFreezed;
import ru.zxspectrum.assembler.compiler.dummy.NamespaceApiFreezed;
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
import ru.zxspectrum.assembler.util.PushbackIterator;
import ru.zxspectrum.assembler.util.PushbackIteratorImpl;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static ru.zxspectrum.assembler.util.TypeUtil.isAddressOffsetPattern;
import static ru.zxspectrum.assembler.util.TypeUtil.isIntegerPattern;
import static ru.zxspectrum.assembler.util.TypeUtil.isOffsetPattern;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class ParameterizedCommandCompiler implements CommandCompiler {
    protected NamespaceApi namespaceApi;

    protected SettingsApi settingsApi;

    protected CompilerApi compilerApi;

    protected String codePattern;

    protected LexemSequence commandPattern;

    protected ByteCodeCompiler byteCodeCompiler;

    public ParameterizedCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi
            , @NonNull CompilerApi compilerApi
            , @NonNull String codePattern, @NonNull LexemSequence commandPattern) {
        if (codePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("codePattern is null or empty");
        }
        this.namespaceApi = namespaceApi;
        this.settingsApi = settingsApi;
        this.codePattern = codePattern;
        this.compilerApi = compilerApi;
        this.commandPattern = commandPattern;
        this.byteCodeCompiler = new ByteCodeCompiler(codePattern, settingsApi.getByteOrder());
    }

    @Override
    public byte[] compile(@NonNull LexemSequence command) {
        CommandMatcher matcher = new CommandMatcher(commandPattern);
        if (!matcher.match(command)) {
            return null;
        }
        List<BigInteger> parameters = compileCommand(command);
        return byteCodeCompiler.compile(parameters.toArray(new BigInteger[parameters.size()]));
    }

    protected BigInteger compileExpressionNumber(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
        final Expression.Result result = expression.evaluate(commandLexem);
        commandIterator.back();
        if (result.isUndefined()) {
            namespaceApi.addToQueue(new PostCommandCompiler(createPostCommandCompiler(), currentCodeOffset
                    , command));
            argumentCommandList.add(BigInteger.ZERO);
            return BigInteger.ZERO;

        } else {
            BigInteger value = result.getValue();
            try {
                final Type srcType = TypeUtil.typeOf(value);
                value = TypeConverter.convert(srcType, value, expectedType, settingsApi.isStrictConversion());
            } catch (ConversationException e) {
                log.error(e.getMessage(), e);
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.getValue().toString());
            }
            if (!result.getValue().equals(value)) {
                Output.throwWarning(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                                .getMessage(MessageList.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                        , value.toString());
            }
            argumentCommandList.add(value);
            return value;
        }
    }

    protected void compileExpressionOffset(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
        final Expression.Result address = expression.evaluate(commandLexem);
        commandIterator.back();
        if (address.isUndefined()) {
            namespaceApi.addToQueue(new PostCommandCompiler(createPostCommandCompiler(), currentCodeOffset
                    , command));
            argumentCommandList.add(BigInteger.ZERO);
        } else {
            final BigInteger offset = address.getValue().subtract(namespaceApi.getAddress())
                    .subtract(currentCodeOffset.add(BigInteger.valueOf(byteCodeCompiler
                            .getArgOffset(argumentCommandList.size()))));
            if (!TypeUtil.isInRange(expectedType, offset)) {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_OUT_OF_RANGE), offset.toString());
            }
            argumentCommandList.add(offset);
        }
    }

    protected void compileVariable(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        if (isAddressOffsetPattern(patternLexem.getValue())) {
            compileExpressionOffset(command, patternLexem, commandLexem, commandIterator, argumentCommandList);
        } else {
            if (isIntegerPattern(patternLexem.getValue()) || isOffsetPattern(patternLexem.getValue())) {
                compileExpressionNumber(command, patternLexem, commandLexem, commandIterator, argumentCommandList);
            } else {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.INTERNAL_ERROR), patternLexem.getValue());
            }
        }
    }

    protected List<BigInteger> compileCommand(LexemSequence command) {
        List<BigInteger> commandArgumentList = new LinkedList<>();
        Iterator<Lexem> patternIterator = commandPattern.get().iterator();
        PushbackIterator<Lexem> commandIterator = new PushbackIteratorImpl(command.get().iterator());
        while (patternIterator.hasNext()) {
            Lexem patternLexem = patternIterator.next();
            Lexem commandLexem = commandIterator.next();
            if (patternLexem.getType() == LexemType.VARIABLE) {
                compileVariable(command, patternLexem, commandLexem, commandIterator, commandArgumentList);
            } else {
                if (!patternLexem.equals(commandLexem)) {
                    throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.INTERNAL_ERROR), commandLexem.getValue());
                }
            }
        }
        return commandArgumentList;
    }

    protected CommandCompiler createPostCommandCompiler() {
        return new PostParameterizedCommandCompiler(new NamespaceApiFreezed(namespaceApi), settingsApi
                , new CompilerApiFreezed(compilerApi), codePattern, commandPattern);
    }

    @Override
    public String toString() {
        return commandPattern.getCaption();
    }


}
