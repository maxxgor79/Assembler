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
import ru.zxspectrum.assembler.error.UndefinedLabelException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lang.Type;
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
 * @Author Maxim Gorin
 */
@Slf4j
public class ParameterizedCommandCompiler implements CommandCompiler {
    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

    private String codePattern;

    private LexemSequence commandPattern;

    private ByteCodeCompiler byteCodeCompiler;

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

    private BigInteger compileExpressionNumber(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        BigInteger value = BigInteger.ZERO;
        try {
            final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
            value = expression.evaluate(commandLexem);
            commandIterator.rollback();
            if (!TypeUtil.isInRange(expectedType, value)) {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_OUT_OF_RANGE), value.toString());
            }
        } catch (UndefinedLabelException e) {
            namespaceApi.addToList(new PostCommandCompiler(cloneFreezed(), currentCodeOffset
                    , command));
        }
        argumentCommandList.add(value);
        return value;
    }

    private void compileExpressionOffset(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        try {
            final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
            final BigInteger address = expression.evaluate(commandLexem);
            final BigInteger offset = address.subtract(namespaceApi.getAddress())
                    .subtract(namespaceApi.getCurrentCodeOffset().add(BigInteger.valueOf(byteCodeCompiler
                            .getArgOffset(argumentCommandList.size()))));
            commandIterator.rollback();
            if (!TypeUtil.isInRange(expectedType, offset)) {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_OUT_OF_RANGE), offset.toString());
            }
            argumentCommandList.add(offset);
        } catch (UndefinedLabelException e) {
            namespaceApi.addToList(new PostCommandCompiler(cloneFreezed(), currentCodeOffset
                    , command));
            argumentCommandList.add(BigInteger.ZERO);
        }
    }

    private void compileVariable(LexemSequence command, Lexem patternLexem, Lexem commandLexem
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

    private List<BigInteger> compileCommand(LexemSequence command) {
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

    protected CommandCompiler cloneFreezed() {
        return new ParameterizedCommandCompiler(new NamespaceApiFreezed(namespaceApi), settingsApi
                , new CompilerApiFreezed(compilerApi), codePattern, commandPattern);
    }

    @Override
    public String toString() {
        return commandPattern.getCaption();
    }
}
