package ru.assembler.core.syntax;

import lombok.NonNull;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemIterator;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.util.RepeatableIteratorImpl;

import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public class ExpressionConsumer {
    private final RepeatableIteratorImpl<Lexem> lexemIterator;

    private ExpressionConsumer() {
        lexemIterator = null;
    }

    public ExpressionConsumer(@NonNull Iterator<Lexem> iterator) {
        this.lexemIterator = new LexemIterator(iterator);
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    public Lexem evaluate() {
        Lexem lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        if (lexem == null) {
            return null;
        }
        return evaluateBitOr(lexem);
    }

    public Lexem evaluate(@NonNull Lexem lexem) {
        return evaluateBitOr(lexem);
    }

    private Lexem evaluateBitOr(Lexem lexem) {
        lexem = evaluateBitXor(lexem);
        while (!isNull(lexem) && lexem.getType() == LexemType.PIPE) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateBitXor(lexem);
        }
        return lexem;
    }

    private Lexem evaluateBitXor(Lexem lexem) {
        lexem = evaluateBitAnd(lexem);
        while (!isNull(lexem) && lexem.getType() == LexemType.CARET) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateBitAnd(lexem);
        }
        return lexem;
    }

    private Lexem evaluateBitAnd(Lexem lexem) {
        lexem = evaluateBitShift(lexem);
        while (!isNull(lexem) && lexem.getType() == LexemType.AMPERSAND) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateBitShift(lexem);
        }
        return lexem;
    }

    private Lexem evaluateBitShift(Lexem lexem) {
        lexem = evaluateAdditive(lexem);
        while (!isNull(lexem) && (lexem.getType() == LexemType.LSHIFT || lexem.getType() == LexemType.RSHIFT)) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateAdditive(lexem);
        }
        return lexem;
    }

    private Lexem evaluateAdditive(Lexem lexem) {
        lexem = evaluateMultiplicative(lexem);
        while (!isNull(lexem) && (lexem.getType() == LexemType.PLUS || lexem.getType() == LexemType.MINUS)) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateMultiplicative(lexem);
        }
        return lexem;
    }

    private Lexem evaluateMultiplicative(Lexem lexem) {
        lexem = evaluateUnary(lexem);
        while (!isNull(lexem) && (lexem.getType() == LexemType.STAR || lexem.getType() == LexemType.SLASH ||
                lexem.getType() == LexemType.PERCENT)) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            lexem = evaluateUnary(lexem);
        }
        return lexem;
    }

    private Lexem evaluateUnary(Lexem lexem) {
        if (!isNull(lexem) && (lexem.getType() == LexemType.MINUS || lexem.getType() == LexemType.PLUS ||
                lexem.getType() == LexemType.TILDE)) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
        }
        return evaluateExpression(lexem);
    }

    private Lexem evaluateExpression(Lexem lexem) {
        if (lexemIterator.hasNext() && lexem.getType() == LexemType.OPEN_BRACE) {
            lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            if (isNull(lexem) || lexem.getType() == LexemType.CLOSED_BRACE || lexem.getType() == LexemType
                    .EOF || lexem.getType() == LexemType.EOL) {
                return lexem;
            }
            lexem = evaluate(lexem);
            if (!isNull(lexem) && lexem.getType() == LexemType.CLOSED_BRACE) {
                lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
            }
            return lexem;
        }
        return evaluateValue(lexem);
    }

    private Lexem evaluateValue(Lexem lexem) {
        if (!isNull(lexem)) {
            switch (lexem.getType()) {
                case CHAR, BINARY, OCTAL, DECIMAL, HEXADECIMAL, IDENTIFIER, VARIABLE -> {
                    lexem = lexemIterator.hasNext() ? lexemIterator.next() : null;
                }
                default -> {
                    lexem = null;
                }
            }
        }
        return lexem;
    }
}