package ru.assembler.core.syntax;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.util.AnalyzerIterator;
import ru.assembler.core.util.ConcatableIterator;
import ru.assembler.core.util.RepeatableIterator;

import java.io.File;
import java.util.*;

/**
 * @author Maxim Gorin
 */
public class SyntaxAnalyzer implements Iterable<LexemSequence> {

    private ConcatableIterator<Lexem> lexemIterator;

    private final LexemSequenceInternalIterator lexemSequenceInternalIterator = new LexemSequenceInternalIterator();

    private final Map<File, Integer> fileLineMap = new HashMap<>();

    private SyntaxAnalyzer() {

    }

    public SyntaxAnalyzer(@NonNull RepeatableIterator<Lexem> lexemIterator) {
        this.lexemIterator = new ConcatableIterator<>(lexemIterator);
    }

    protected void processComment(@NonNull Lexem lexem) {
        //do nothing
    }

    protected void processEOL(@NonNull Lexem lexem) {
        //do nothing
    }

    protected void processEOF(@NonNull Lexem lexem) {
        //do nothing
    }

    protected LexemSequence processSequence(Lexem lexem) {
        final List<Lexem> lexemList = new LinkedList<>();
        lexemList.add(lexem);
        if (lexem.getType() != LexemType.LABEL) {
            while (lexemIterator.hasNext()) {
                lexem = lexemIterator.next();
                if (lexem.getType() == LexemType.EOL
                        || lexem.getType() == LexemType.EOF
                        || lexem.getType() == LexemType.COMMENT) {
                    break;
                }
                lexemList.add(lexem);
            }
        }
        return new LexemSequence(lexemList);
    }

    private LexemSequence getNext() {
        while (lexemIterator.hasNext()) {
            Lexem lexem = lexemIterator.next();
            putLineNumber(lexem);
            if (lexem.getType() == LexemType.EOF) {
                processEOF(lexem);
            } else if (lexem.getType() == LexemType.COMMENT) {
                processComment(lexem);
            } else {
                if (lexem.getType() == LexemType.EOL) {
                    processEOL(lexem);
                } else {
                    return processSequence(lexem);
                }
            }
        }
        return null;
    }

    private void putLineNumber(Lexem lexem) {
        if (lexem.getFd() == null || lexem.getFd().getFile() == null) {
            return;
        }
        final Integer lineNumber = fileLineMap.get(lexem.getFd().getFile());
        if (lineNumber == null || lexem.getLineNumber() > lineNumber) {
            fileLineMap.put(lexem.getFd().getFile(), lexem.getLineNumber());
        }
    }

    public int getLineCount() {
        int count = 0;
        for (Map.Entry<File, Integer> entry : fileLineMap.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    @Override
    public Iterator<LexemSequence> iterator() {
        return lexemSequenceInternalIterator;
    }

    public void append(@NonNull RepeatableIterator<Lexem> lexemIterator) {
        this.lexemIterator.concat(lexemIterator);
    }

    private class LexemSequenceInternalIterator extends AnalyzerIterator<LexemSequence> {

        @Override
        protected LexemSequence externalNext() {
            return getNext();
        }

        @Override
        protected boolean externalHasNext() {
            return true;
        }
    }
}
