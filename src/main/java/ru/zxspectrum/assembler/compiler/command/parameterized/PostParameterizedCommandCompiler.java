package ru.zxspectrum.assembler.compiler.command.parameterized;

import lombok.NonNull;
import ru.zxspectrum.assembler.compiler.CompilerApi;
import ru.zxspectrum.assembler.compiler.PostCommandCompiler;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lang.Type;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.ns.NamespaceApi;
import ru.zxspectrum.assembler.settings.SettingsApi;
import ru.zxspectrum.assembler.syntax.Expression;
import ru.zxspectrum.assembler.syntax.LexemSequence;
import ru.zxspectrum.assembler.util.PushbackIterator;
import ru.zxspectrum.assembler.util.TypeUtil;

import java.math.BigInteger;
import java.util.List;

class PostParameterizedCommandCompiler extends ParameterizedCommandCompiler {
    public PostParameterizedCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi, @NonNull String codePattern, @NonNull LexemSequence commandPattern) {
        super(namespaceApi, settingsApi, compilerApi, codePattern, commandPattern);
    }

    @Override
    protected BigInteger compileExpressionNumber(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
        final Expression.Result result = expression.evaluate(commandLexem);
        commandIterator.back();
        if (result.isUndefined()) {
            throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNKNOWN_IDENTIFIER), result.getUnknown().getValue());

        } else {
            if (!TypeUtil.isInRange(expectedType, result.getValue())) {
                throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                        .getMessage(MessageList.VALUE_OUT_OF_RANGE), result.toString());
            }
            argumentCommandList.add(result.getValue());
            return result.getValue();
        }
    }

    @Override
    protected void compileExpressionOffset(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final BigInteger currentCodeOffset = namespaceApi.getCurrentCodeOffset();
        final Expression expression = new Expression(compilerApi.getFile(), commandIterator, namespaceApi);
        final Expression.Result address = expression.evaluate(commandLexem);
        commandIterator.back();
        if (address.isUndefined()) {
            throw new CompilerException(compilerApi.getFile(), commandLexem.getLineNumber(), MessageList
                    .getMessage(MessageList.UNKNOWN_IDENTIFIER), address.getUnknown().getValue());
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
}
