package ru.zxspectrum.assembler.compiler.command.parameterized;

import ru.zxspectrum.assembler.NamespaceApi;
import ru.zxspectrum.assembler.compiler.CommandCompiler;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.compiler.bytecode.ByteCodeCompiler;
import ru.zxspectrum.assembler.compiler.dummy.CompilerApiFreezed;
import ru.zxspectrum.assembler.compiler.dummy.NamespaceApiFreezed;
import ru.zxspectrum.assembler.error.AssemblerException;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.error.text.Output;
import ru.zxspectrum.assembler.lang.Type;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
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
import static ru.zxspectrum.assembler.util.TypeUtil.isAddressPattern;
import static ru.zxspectrum.assembler.util.TypeUtil.isIntegerPattern;
import static ru.zxspectrum.assembler.util.TypeUtil.isOffsetPattern;

/**
 * @Author Maxim Gorin
 */
public class ParameterizedCommandCompiler implements CommandCompiler {
    private NamespaceApi namespaceApi;

    private SettingsApi settingsApi;

    private CompilerApi compilerApi;

    private String codePattern;

    private LexemSequence commandPattern;

    private ByteCodeCompiler byteCodeCompiler;

    public ParameterizedCommandCompiler(NamespaceApi namespaceApi, SettingsApi settingsApi, CompilerApi compilerApi
            , String codePattern, LexemSequence commandPattern) {
        if (namespaceApi == null) {
            throw new IllegalArgumentException("namespaceApi is null");
        }
        this.namespaceApi = namespaceApi;
        if (settingsApi == null) {
            throw new NullPointerException("settingsApi");
        }
        this.settingsApi = settingsApi;
        if (codePattern == null || codePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("codePattern is null or empty");
        }
        this.codePattern = codePattern;
        if (compilerApi == null) {
            throw new NullPointerException("compilerApi");
        }
        this.compilerApi = compilerApi;
        if (commandPattern == null) {
            throw new IllegalArgumentException("codePattern is null");
        }
        this.commandPattern = commandPattern;
        this.byteCodeCompiler = new ByteCodeCompiler(codePattern, settingsApi.getByteOrder());
    }

    @Override
    public byte[] compile(LexemSequence command, boolean ignoreLabel) {
        if (command == null) {
            return null;
        }
        CommandMatcher matcher = new CommandMatcher(commandPattern);
        if (!matcher.match(command)) {
            return null;
        }
        List<BigInteger> parameters = compileCommand(command, ignoreLabel);
        return byteCodeCompiler.compile(parameters.toArray(new BigInteger[parameters.size()]));
    }

    private BigInteger compileExpression(Lexem patternLexem, Lexem commandLexem, PushbackIterator<Lexem> commandIterator
            , List<BigInteger> argumentCommandList) {
        Type expectedType = TypeUtil.toType(patternLexem.getValue());

        Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
        BigInteger address = expression.evaluate(commandLexem);
        commandIterator.rollback();
        if (!TypeUtil.isInRange(expectedType, address)) {
            Output.throwWarning(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.VALUE_OUT_OF_RANGE), address.toString(), expectedType.toString());
        }
        argumentCommandList.add(address);
        return address;
    }

    private void compileAddress(Lexem patternLexem, Lexem commandLexem, PushbackIterator<Lexem> commandIterator
            , List<BigInteger> argumentCommandList) {
        Type expectedType = TypeUtil.toType(patternLexem.getValue());
        Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi, false);
        BigInteger offset = expression.evaluate(commandLexem);
        commandIterator.rollback();
        if (TypeUtil.isAddressPattern(patternLexem.getValue())) {
            final BigInteger address = offset.add(namespaceApi.getAddress());
            if (!TypeUtil.isInRange(expectedType, address)) {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), address.toString());
            }
            argumentCommandList.add(address);
        } else {
            if (TypeUtil.isAddressOffsetPattern(patternLexem.getValue())) {
                final BigInteger correctedOffset = offset.subtract(namespaceApi.getCurrentCodeOffset().add(BigInteger
                        .valueOf(byteCodeCompiler.getSize())));
                if (!TypeUtil.isInRange(expectedType, correctedOffset)) {
                    throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                            .getMessage(MessageList.ADDRESS_OUT_OF_RANGE), correctedOffset.toString());
                }
                argumentCommandList.add(correctedOffset);
            } else {
                throw new AssemblerException("Invalid pattern type");
            }
        }
    }

    private void compileAddress(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList, boolean ignoreLabel) {
        if (ignoreLabel) {
            namespaceApi.addToList(new PostCommandCompiler(cloneFreezed(), namespaceApi.getCurrentCodeOffset()
                    , command));
            argumentCommandList.add(BigInteger.ZERO);
        } else {
            compileAddress(patternLexem, commandLexem, commandIterator, argumentCommandList);
        }
    }

    private void compileVariable(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList, boolean ignoreLabel) {
        if (isAddressPattern(patternLexem.getValue()) || isAddressOffsetPattern(patternLexem.getValue())) {
            compileAddress(command, patternLexem, commandLexem, commandIterator, argumentCommandList, ignoreLabel);
        } else {
            if (isIntegerPattern(patternLexem.getValue()) || isOffsetPattern(patternLexem.getValue())) {
                compileExpression(patternLexem, commandLexem, commandIterator, argumentCommandList);
            } else {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.INTERNAL_ERROR), patternLexem.getValue());
            }
        }
    }

    private List<BigInteger> compileCommand(LexemSequence command, boolean ignoreLabel) {
        List<BigInteger> commandArgumentList = new LinkedList<>();
        Iterator<Lexem> patternIterator = commandPattern.get().iterator();
        PushbackIterator<Lexem> commandIterator = new PushbackIteratorImpl(command.get().iterator());
        while (patternIterator.hasNext()) {
            Lexem patternLexem = patternIterator.next();
            Lexem commandLexem = commandIterator.next();
            if (patternLexem.getType() == LexemType.VARIABLE) {
                compileVariable(command, patternLexem, commandLexem, commandIterator, commandArgumentList, ignoreLabel);
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
