package ru.assembler.core.compiler.command.parameterized;

import lombok.NonNull;
import ru.assembler.core.compiler.CompilerApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.ConversationException;
import ru.assembler.core.error.text.Messages;
import ru.assembler.core.io.Output;
import ru.assembler.core.lang.Type;
import ru.assembler.core.lang.TypeConverter;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.settings.SettingsApi;
import ru.assembler.core.syntax.Expression;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.PushbackIterator;
import ru.assembler.core.util.TypeUtil;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Maxim Gorin
 */
class PostParameterizedCommandCompiler extends ParameterizedCommandCompiler {
    public PostParameterizedCommandCompiler(@NonNull NamespaceApi namespaceApi, @NonNull SettingsApi settingsApi, @NonNull CompilerApi compilerApi, @NonNull String codePattern, @NonNull LexemSequence commandPattern) {
        super(namespaceApi, settingsApi, compilerApi, codePattern, commandPattern);
    }

    @Override
    protected BigInteger compileExpressionNumber(LexemSequence command, Lexem patternLexem, Lexem commandLexem
            , PushbackIterator<Lexem> commandIterator, List<BigInteger> argumentCommandList) {
        final Type expectedType = TypeUtil.toType(patternLexem.getValue());
        final Expression expression = new Expression(commandLexem.getFd(), commandIterator, namespaceApi);
        final Expression.Result result = expression.evaluate(commandLexem);
        commandIterator.back();
        if (result.isUndefined()) {
            throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                    .getMessage(Messages.UNKNOWN_IDENTIFIER), result.getLexem().getValue());
        } else {
            BigInteger value = result.getValue();
            try {
                final Type srcType = TypeUtil.typeOf(value);
                value = TypeConverter.convert(srcType, value, expectedType, settingsApi.isStrictConversion());
            } catch (ConversationException e) {
                throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                        .getMessage(Messages.VALUE_OUT_OF_RANGE), result.getValue().toString());
            }
            if (!result.getValue().equals(value)) {
                Output.throwWarning(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                                .getMessage(Messages.LOSS_PRECISION_TYPE_FOR), result.getValue().toString()
                        , value.toString());
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
        final Expression expression = new Expression(commandLexem.getFd(), commandIterator, namespaceApi);
        final Expression.Result address = expression.evaluate(commandLexem);
        commandIterator.back();
        if (address.isUndefined()) {
            throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                    .getMessage(Messages.UNKNOWN_IDENTIFIER), address.getLexem().getValue());
        } else {
            final BigInteger offset = address.getValue().subtract(namespaceApi.getAddress())
                    .subtract(currentCodeOffset.add(BigInteger.valueOf(byteCodeCompiler
                            .getArgOffset(argumentCommandList.size()))));
            if (!TypeUtil.isInRange(expectedType, offset)) {
                throw new CompilerException(commandLexem.getFd(), commandLexem.getLineNumber(), Messages
                        .getMessage(Messages.VALUE_OUT_OF_RANGE), offset.toString());
            }
            argumentCommandList.add(offset);
        }
    }
}
