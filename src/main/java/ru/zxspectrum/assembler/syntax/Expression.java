package ru.zxspectrum.assembler.syntax;

import lombok.NonNull;
import ru.zxspectrum.assembler.NamespaceApi;
import ru.zxspectrum.assembler.error.CompilerException;
import ru.zxspectrum.assembler.error.DividingByZeroException;
import ru.zxspectrum.assembler.error.ExpressionException;
import ru.zxspectrum.assembler.error.text.MessageList;
import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemIterator;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.util.Converter;
import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * @Author Maxim Gorin
 */
public class Expression {
    private RepeatableIteratorImpl<Lexem> lexemIterator;

    private File file;

    private NamespaceApi namespaceApi;

    private Lexem lastLexem;

    private boolean onlyConst;

    private Expression() {
        lexemIterator = null;
    }

    public Expression(File file, Iterator<Lexem> iterator, NamespaceApi namespaceApi) {
        this(file, iterator, namespaceApi, true);
    }

    public Expression(@NonNull File file, @NonNull Iterator<Lexem> iterator, @NonNull NamespaceApi namespaceApi
            , boolean onlyConst) {
        this.file = file;
        this.lexemIterator = new LexemIterator(iterator);
        this.namespaceApi = namespaceApi;
        this.onlyConst = onlyConst;
    }

    public BigInteger evaluate() {
        Lexem lexem = lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (lexem == null) {
            return null;
        }
        return evaluate(lexem);
    }

    public BigInteger evaluate(@NonNull Lexem lexem) {
        return evaluateBitOr(lexem);
    }

    private BigInteger evaluateBitOr(Lexem lexem) {
        BigInteger result = evaluateBitXor(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.PIPE) {
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateBitXor(lexem);
            result = result.or(result2);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateBitXor(Lexem lexem) {
        BigInteger result = evaluateBitAnd(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.CARET) {
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateBitAnd(lexem);
            result = result.xor(result2);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateBitAnd(Lexem lexem) {
        BigInteger result = evaluateBitShift(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && lexem.getType() == LexemType.AMPERSAND) {
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateBitShift(lexem);
            result = result.and(result2);
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateBitShift(Lexem lexem) {
        BigInteger result = evaluateAdditive(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.LSHIFT || lexem.getType() == LexemType.RSHIFT)) {
            LexemType oper = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateAdditive(lexem);
            switch (oper) {
                case LSHIFT -> {
                    result = result.shiftLeft(result2.intValue());
                }
                case RSHIFT -> {
                    result = result.shiftRight(result2.intValue());
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateAdditive(Lexem lexem) {
        BigInteger result = evaluateMultiplicative(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.PLUS || lexem.getType() == LexemType.MINUS)) {
            LexemType oper = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateMultiplicative(lexem);
            switch (oper) {
                case PLUS -> {
                    result = result.add(result2);
                }
                case MINUS -> {
                    result = result.subtract(result2);
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateMultiplicative(Lexem lexem) {
        BigInteger result = evaluateUnary(lexem);
        lexem = lexemIterator.current();
        while (lexemIterator.hasNext() && (lexem.getType() == LexemType.STAR || lexem.getType() == LexemType.SLASH ||
                lexem.getType() == LexemType.PERCENT)) {
            LexemType type = lexem.getType();
            lexem = lastLexem = lexemIterator.next();
            BigInteger result2 = evaluateUnary(lexem);
            switch (type) {
                case STAR -> {
                    result = result.multiply(result2);
                }
                case SLASH -> {
                    try {
                        result = result.divide(result2);
                    } catch (ArithmeticException e) {
                        throw new DividingByZeroException(file, lexem.getLineNumber());
                    }
                }
                case PERCENT -> {
                    try {
                        result = result.mod(result2);
                    } catch (ArithmeticException e) {
                        throw new DividingByZeroException(file, lexem.getLineNumber());
                    }
                }
            }
            lexem = lexemIterator.current();
        }
        return result;
    }

    private BigInteger evaluateUnary(Lexem lexem) {
        BigInteger result;
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
                    result = result.abs();
                }
                case MINUS -> {
                    result = result.negate();
                }
                case TILDE -> {
                    result = result.not();
                }
            }
        }
        return result;
    }

    private BigInteger evaluateExpression(Lexem lexem) {
        if (lexemIterator.hasNext() && lexem.getType() == LexemType.OPEN_BRACE) {
            lexem = lastLexem = lexemIterator.next();
            if (lexem.getType() == LexemType.CLOSED_BRACE || lexem.getType() == LexemType.EOS || lexem.getType() == LexemType.EOL) {
                throw new ExpressionException(file, lexem.getLineNumber(), MessageList.getMessage(MessageList.IS_EXPECTED),
                        MessageList.getMessage(MessageList.EXPRESSION));
            }
            BigInteger result = evaluate(lexem);
            lexem = lexemIterator.current();
            if (lexem.getType() == LexemType.CLOSED_BRACE) {
                lexem = lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            } else {
                throw new ExpressionException(file, lexem.getLineNumber(), MessageList.getMessage(MessageList.IS_EXPECTED),
                        ")");
            }
            return result;
        }
        return evaluateValue(lexem);
    }

    private BigInteger evaluateValue(Lexem lexem) {
        BigInteger result;
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
                    throw new CompilerException(file, lexem.getLineNumber(), MessageList.getMessage(MessageList
                            .UNEXPECTED_LABEL), lexem.getValue());
                }

            }

            default -> {
                throw new CompilerException(file, lexem.getLineNumber(),
                        MessageList.getMessage(MessageList.UNEXPECTED_SYMBOL), lexem.getValue());
            }
        }
        return result;
    }

    private BigInteger evaluateChar(Lexem lexem) {
        BigInteger result = Converter.charToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return result;
    }

    private BigInteger evaluateBinary(Lexem lexem) {
        BigInteger result = Converter.binaryToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return result;
    }

    private BigInteger evaluateOctal(Lexem lexem) {
        BigInteger result = Converter.octalToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return result;
    }

    private BigInteger evaluateDecimal(Lexem lexem) {
        BigInteger result = Converter.decimalToBigInteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return result;
    }

    private BigInteger evaluateHexadecimal(Lexem lexem) {
        BigInteger result = Converter.hexadecimalToBiginteger(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        return result;
    }

    private BigInteger evaluateLabel(Lexem lexem) {
        BigInteger result = namespaceApi.getLabelCodeOffset(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (result == null) {
            throw new CompilerException(file, lexem.getLineNumber(), MessageList
                    .getMessage(MessageList.LABEL_NOT_FOUND), lexem.getValue());
        }
        return result.add(namespaceApi.getAddress());//absolut address
    }

    private BigInteger evaluateVariable(Lexem lexem) {
        BigInteger result = namespaceApi.getVariableValue(lexem.getValue());
        lastLexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (result == null) {
            throw new CompilerException(file, lexem.getLineNumber(), MessageList
                    .getMessage(MessageList.VARIABLE_NOT_FOUND), lexem.getValue());
        }
        return result;
    }

    public Lexem getLastLexem() {
        return lastLexem;
    }
}
