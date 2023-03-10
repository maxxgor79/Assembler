package ru.zxspectrum.assembler.syntax;

import ru.zxspectrum.assembler.lexem.Lexem;
import ru.zxspectrum.assembler.lexem.LexemType;
import ru.zxspectrum.assembler.util.AnalyzerIterator;
import ru.zxspectrum.assembler.util.RepeatableIterator;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
public class SyntaxAnalyzer implements Iterable<LexemSequence> {
    private RepeatableIterator<Lexem> lexemIterator;

    private LexemSequenceInternalIterator lexemSequenceInternalIterator;

    private int lineCount;

    private SyntaxAnalyzer() {

    }

    public SyntaxAnalyzer(RepeatableIterator<Lexem> lexemIterator) {
        if (lexemIterator == null) {
            throw new IllegalArgumentException("lexemIterator is null");
        }
        this.lexemIterator = lexemIterator;
    }

    private void processComment(Lexem lexem) {
        //do nothing
    }

    private void processEOL(Lexem lexem) {
        //do nothing
    }

    private LexemSequence processSequence(Lexem lexem) {
        List<Lexem> lexemList = new LinkedList<>();
        lexemList.add(lexem);
        if (lexem.getType() != LexemType.LABEL) {
            while (lexemIterator.hasNext()) {
                lexem = lexemIterator.next();
                if (lexem.getType() == LexemType.EOL || lexem.getType() == LexemType.EOS ||
                        lexem.getType() == LexemType.COMMENT) {
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
            lineCount = Math.max(lineCount, lexem.getLineNumber());
            if (lexem.getType() == LexemType.EOS) {
                return null;
            }
            if (lexem.getType() == LexemType.COMMENT) {
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

    @Override
    public Iterator<LexemSequence> iterator() {
        if (lexemSequenceInternalIterator == null) {
            lexemSequenceInternalIterator = new LexemSequenceInternalIterator();
        }
        return lexemSequenceInternalIterator;
    }

    public int getLineCount() {
        return lineCount;
    }

    private class LexemSequenceInternalIterator extends AnalyzerIterator<LexemSequence> {

        @Override
        protected LexemSequence externalNext() throws IOException {
            return getNext();
        }

        @Override
        protected boolean externalHasNext() {
            return true;
        }
    }
}
