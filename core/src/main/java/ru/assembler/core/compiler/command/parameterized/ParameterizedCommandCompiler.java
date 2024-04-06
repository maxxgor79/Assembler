package ru.assembler.core.compiler.command.parameterized;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.core.compiler.CommandCompiler;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.compiler.PostCommandCompiler;
import ru.assembler.core.compiler.bytecode.ByteCodeCompiler;
import ru.assembler.core.compiler.dummy.CompilerApiFreezed;
import ru.assembler.core.compiler.dummy.NamespaceApiFreezed;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.ConversationException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.Output;
import ru.assembler.core.lang.Type;
import ru.assembler.core.lang.TypeConverter;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.PushbackIterator;
import ru.assembler.core.util.PushbackIteratorImpl;
import ru.assembler.core.util.TypeUtil;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static ru.assembler.core.util.TypeUtil.*;

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

    public ParameterizedCommandCompiler(@NonNull NamespaceApi namespaceApi,
                                        @NonNull SettingsApi settingsApi
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
    public String[] names() {
        return null;
    }

    @Override
    public byte[] compile(@NonNull LexemSequence command) {
        final CommandMatcher matcher = new CommandMatcher(commandPattern);
        if (!matcher.match(command)) {
            return null;
        }
        List<BigInteger> parameters = compileCommand(command);
        return byteCodeCompiler.compile(parameters.toArray(new BigInteger[parameters.size()]));
    }

    protected BigInteger compileExpressionNumber(LexemSequence command, Lexem patternLexem,
                                                 Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        final Expression expression = new Expression(commandLexem.getFd(), commandIterator,
                namespaceApi);
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
                value = TypeConverter.convert(srcType, value, expectedType,
                        settingsApi.isStrictConversion());
            } catch (ConversationException e) {
                log.error(e.getMessage(), e);
                throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                        .getMessage(Messages.VALUE_OUT_OF_RANGE), result.getValue().toString());
            }
            if (!result.getValue().equals(value)) {
                Output.throwWarning(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                                .getMessage(Messages.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                        , value.toString());
            }
            argumentCommandList.add(value);
            return value;
        }
    }

    protected void compileExpressionOffset(LexemSequence command, Lexem patternLexem,
                                           Lexem commandLexem, PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        final Expression expression = new Expression(commandLexem.getFd(), commandIterator,
                namespaceApi);
        final Expression.Result address = expression.evaluate(commandLexem);
        commandIterator.back();
        if (address.isUndefined()) {
            namespaceApi.addToQueue(new PostCommandCompiler(createPostCommandCompiler(), currentCodeOffset
                    , command));
            argumentCommandList.add(BigInteger.ZERO);
        } else {
            //only for labels
            BigInteger offset;
            if (address.getValue().compareTo(namespaceApi.getAddress()) >= 0) {
                offset = address.getValue().subtract(namespaceApi.getAddress())
                        .subtract(currentCodeOffset.add(BigInteger.valueOf(byteCodeCompiler
                                .getArgOffset(argumentCommandList.size()))));
            } else {
                offset = address.getValue();
            }
            if (!TypeUtil.isInRange(expectedType, offset)) {
                throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                        .getMessage(Messages.VALUE_OUT_OF_RANGE), offset.toString());
            }
            argumentCommandList.add(offset);
        }
    }

    protected void compileVariable(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        if (isAddressOffsetPattern(patternLexem.getValue())) {
            compileExpressionOffset(command, patternLexem, commandLexem, commandIterator,
                    argumentCommandList);
        } else {
            if (isIntegerPattern(patternLexem.getValue()) || isOffsetPattern(patternLexem.getValue())) {
                compileExpressionNumber(command, patternLexem, commandLexem, commandIterator,
                        argumentCommandList);
            } else {
                throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                        .getMessage(Messages.INTERNAL_ERROR), patternLexem.getValue());
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
                    throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(),
                            Messages
                                    .getMessage(Messages.INTERNAL_ERROR), commandLexem.getValue());
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
