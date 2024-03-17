package ru.assembler.core.syntax;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.assembler.core.io.FileDescriptor;
import ru.assembler.core.ns.NamespaceApi;
import ru.assembler.core.error.CompilerException;
import ru.assembler.core.error.DividingByZeroException;
import ru.assembler.core.error.ExpressionException;
import ru.assembler.core.error.text.MessageList;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemIterator;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.util.Converter;
import ru.assembler.core.util.RepeatableIteratorImpl;

import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public class Expression {
    private final RepeatableIteratorImpl<Lexem> lexemIterator;

    private FileDescriptor fd;

    private NamespaceApi namespaceApi;

    @Getter
    private Lexem lastLexem;

    private boolean onlyConst;

    private Expression() {
        lexemIterator = null;
    }

    public Expression(FileDescriptor fd, Iterator<Lexem> iterator, NamespaceApi namespaceApi) {
        this(fd, iterator, namespaceApi, false);
    }

    public Expression(@NonNull FileDescriptor fd, @NonNull Iterator<Lexem> iterator, @NonNull NamespaceApi namespaceApi
            , boolean onlyConst) {
        this.fd = fd;
        this.lexemIterator = new LexemIterator(iterator);
        this.namespaceApi = namespaceApi;
        this.onlyConst = onlyConst;
    }

    public Result evaluate() {
        Lexem lexem = lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (lexem == null) {
            return null;
        }
        return evaluate(lexem);
    }

    public Result evaluate(@NonNull Lexem lexem) {
        return evaluateBitOr(lexem);
    }

    private Result evaluateBitOr(Lexem lexem) {
        Result result = evaluateBitXor(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.PIPE) {
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateBitXor(lexem);
            result.value = result.value.or(result2.value);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateBitXor(Lexem lexem) {
        Result result = evaluateBitAnd(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.CARET) {
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateBitAnd(lexem);
            result.value = result.value.xor(result2.value);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateBitAnd(Lexem lexem) {
        Result result = evaluateBitShift(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.AMPERSAND) {
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateBitShift(lexem);
            result.value = result.value.and(result2.value);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateBitShift(Lexem lexem) {
        Result result = evaluateAdditive(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.LSHIFT || lexem.getType() == LexemType.RSHIFT)) {
            LexemType oper = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateAdditive(lexem);
            switch (oper) {
                case LSHIFT -> {
                    result.value = result.value.shiftLeft(result2.value.intValue());
                }
                case RSHIFT -> {
                    result.value = result.value.shiftRight(result2.value.intValue());
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateAdditive(Lexem lexem) {
        Result result = evaluateMultiplicative(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.PLUS || lexem.getType() == LexemType.MINUS)) {
            LexemType oper = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateMultiplicative(lexem);
            switch (oper) {
                case PLUS -> {
                    result.value = result.value.add(result2.value);
                }
                case MINUS -> {
                    result.value = result.value.subtract(result2.value);
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateMultiplicative(Lexem lexem) {
        Result result = evaluateUnary(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.STAR || lexem.getType() == LexemType.SLASH ||
                lexem.getType() == LexemType.PERCENT)) {
            LexemType type = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            Result result2 = evaluateUnary(lexem);
            switch (type) {
                case STAR -> {
                    result.value = result.value.multiply(result2.value);
                }
                case SLASH -> {
                    try {
                        result.value = result.value.divide(result2.value);
                    } catch (ArithmeticException e) {
                        throw new DividingByZeroException(fd, lexem.getLineNumber());
                    }
                }
                case PERCENT -> {
                    try {
                        result.value = result.value.mod(result2.value);
                    } catch (ArithmeticException e) {
                        throw new DividingByZeroException(fd, lexem.getLineNumber());
                    }
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private Result evaluateUnary(Lexem lexem) {
        Result result;
        LexemType oper = null;
        if (lexemIterator.hasNext() && (lexem.getType() == LexemType.MINUS || lexem.getType() == LexemType.PLUS ||
                lexem.getType() == LexemType.TILDE)) {
            oper = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
        }
        result = evaluateExpression(lexem);
        if (oper != null) {
            switch (oper) {
                case PLUS -> {
                    result.value = result.value.abs();
                }
                case MINUS -> {
                    result.value = result.value.negate();
                }
                case TILDE -> {
                    result.value = result.value.not();
                }
            }
        }
        return result;
    }

    private Result evaluateExpression(Lexem lexem) {
        if (lexemIterator.hasNext() && lexem.getType() == LexemType.OPEN_BRACE) {
            lexem = lastLexem = lexemIterator.next();
            if (lexem.getType() == LexemType.CLOSED_BRACE || lexem.getType() == LexemType.EOF
                || lexem.getType() == LexemType.EOL) {
                throw new ExpressionException(fd, lexem.getLineNumber(), MessageList.getMessage(MessageList.IS_EXPECTED),
                        MessageList.getMessage(MessageList.EXPRESSION));
            }
            Result result = evaluate(lexem);
            lexem = lexemIterator.current();
            if (lexem.getType() == LexemType.CLOSED_BRACE) {
                lexem = lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            } else {
                throw new ExpressionException(fd, lexem.getLineNumber(), MessageList.getMessage(MessageList.IS_EXPECTED),
                        ")");
            }
            return result;
        }
        return evaluateValue(lexem);
    }

    private Result evaluateValue(Lexem lexem) {
        Result result;
        switch (lexem.getType()) {
            case CHAR -> {
                result = evaluateChar(lexem);
            }

            case BINARY -> {
                result = evaluateBinary(lexem);
            }

            case OCTAL -> {
                result = evaluateOctal(lexem);
            }

            case DECIMAL -> {
                result = evaluateDecimal(lexem);
            }

            case HEXADECIMAL -> {
                result = evaluateHexadecimal(lexem);
            }

            case VARIABLE -> {
                result = evaluateVariable(lexem);
            }

            case IDENTIFIER -> {
                if (!onlyConst) {
                    result = evaluateLabel(lexem);
                } else {
                    throw new CompilerException(fd, lexem.getLineNumber(), MessageList.getMessage(MessageList
                            .UNEXPECTED_LABEL), lexem.getValue());
                }

            }

            default -> {
                throw new CompilerException(fd, lexem.getLineNumber(),
                        MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL), lexem.getValue());
            }
        }
        return result;
    }

    private Result evaluateChar(Lexem lexem) {
        BigInteger value = Converter.charToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return new Result(value);
    }

    private Result evaluateBinary(Lexem lexem) {
        BigInteger value = Converter.binaryToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return new Result(value);
    }

    private Result evaluateOctal(Lexem lexem) {
        BigInteger value = Converter.octalToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return new Result(value);
    }

    private Result evaluateDecimal(Lexem lexem) {
        BigInteger value = Converter.decimalToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return new Result(value);
    }

    private Result evaluateHexadecimal(Lexem lexem) {
        BigInteger value = Converter.hexadecimalToBiginteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return new Result(value);
    }

    private Result evaluateLabel(Lexem lexem) {
        BigInteger value = namespaceApi.getLabelCodeOffset(lexem.getValue());
        Result result = new Result(lexem);
        if (value == null) {
            value = BigInteger.ZERO;
            result.setUndefined(true);
        }
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        value = value.add(namespaceApi.getAddress());
        result.setValue(value);
        return result;//absolut address
    }

    private Result evaluateVariable(Lexem lexem) {
        BigInteger value = namespaceApi.getVariableValue(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (value == null) {
            throw new CompilerException(fd, lexem.getLineNumber(), MessageList
                    .getMessage(MessageList.VARIABLE_NOT_FOUND), lexem.getValue());
        }
        return new Result(value);
    }

    @Data
    @NoArgsConstructor
    public static class Result {
        Result(@NonNull BigInteger value) {
            this.value = value;
        }

        Result(@NonNull Lexem lexem) {
            this.lexem = lexem;
        }

        @NonNull
        private Lexem lexem;

        @NonNull
        private BigInteger value;

        private boolean undefined;
    }
}
