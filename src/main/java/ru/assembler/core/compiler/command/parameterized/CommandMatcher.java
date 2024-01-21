package ru.assembler.core.compiler.command.parameterized;

import lombok.NonNull;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.syntax.ExpressionConsumer;
import ru.assembler.core.syntax.LexemSequence;
import ru.assembler.core.util.IndexedArrayIterator;
import ru.assembler.core.util.TypeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Maxim Gorin
 */
public class CommandMatcher {
    private final LexemSequence pattern;

    private static final Set<LexemType> expressionLexemTypeSet = new HashSet<>();

    static {
        expressionLexemTypeSet.add(LexemType.CHAR);
        expressionLexemTypeSet.add(LexemType.BINARY);
        expressionLexemTypeSet.add(LexemType.OCTAL);
        expressionLexemTypeSet.add(LexemType.DECIMAL);
        expressionLexemTypeSet.add(LexemType.HEXADECIMAL);
        expressionLexemTypeSet.add(LexemType.MINUS);
        expressionLexemTypeSet.add(LexemType.PLUS);
        expressionLexemTypeSet.add(LexemType.STAR);
        expressionLexemTypeSet.add(LexemType.SLASH);
        expressionLexemTypeSet.add(LexemType.OPEN_BRACE);
        expressionLexemTypeSet.add(LexemType.CLOSED_BRACE);
        expressionLexemTypeSet.add(LexemType.LSHIFT);
        expressionLexemTypeSet.add(LexemType.RSHIFT);
        expressionLexemTypeSet.add(LexemType.TILDE);
        expressionLexemTypeSet.add(LexemType.PIPE);
        expressionLexemTypeSet.add(LexemType.CARET);
        expressionLexemTypeSet.add(LexemType.AMPERSAND);
        expressionLexemTypeSet.add(LexemType.PERCENT);
    }

    public CommandMatcher(@NonNull LexemSequence pattern) {
        this.pattern = pattern;
    }

    private static int skipExpression(List<Lexem> commandList, int commandIndex) {
        IndexedArrayIterator indexedArrayIterator = new IndexedArrayIterator<>(commandList, commandIndex);
        ExpressionConsumer expressionConsumer = new ExpressionConsumer(indexedArrayIterator);
        Lexem lastLexem = expressionConsumer.evaluate();
        if (lastLexem == null) {
            return commandList.size();
        } else {
            return commandList.indexOf(lastLexem);
        }
    }


    public boolean match(LexemSequence command) {
        if (command == null) {
            return false;
        }
        if (command.size() < pattern.size()) {
            return false;
        }
        List<Lexem> patternList = new ArrayList<>(pattern.get());
        List<Lexem> commandList = new ArrayList<>(command.get());
        int commandIndex = 0;
        int patternIndex = 0;
        while (patternIndex < patternList.size() && commandIndex < commandList.size()) {
            Lexem patternLexem = patternList.get(patternIndex);
            Lexem commandLexem = commandList.get(commandIndex);
            if (patternLexem.getType() != LexemType.VARIABLE) {
                if (!patternLexem.equals(commandLexem)) {
                    return false;
                }
                patternIndex++;
                commandIndex++;
            } else {
                if (TypeUtil.isAddressOffsetPattern(patternLexem.getValue())) {
                    patternIndex++;
                    commandIndex = skipExpression(commandList, commandIndex);

                } else {
                    patternIndex++;
                    commandIndex = skipExpression(commandList, commandIndex);
                }
            }
        }
        return patternIndex == patternList.size() && commandIndex == commandList.size();
    }
}
