package ru.assembler.core.syntax;

import lombok.Getter;
import lombok.NonNull;
import ru.assembler.core.lexem.Lexem;
import ru.assembler.core.lexem.LexemType;
import ru.assembler.core.util.AnalyzerIterator;
import ru.assembler.core.util.ConcatableIterator;
import ru.assembler.core.util.RepeatableIterator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public class SyntaxAnalyzer implements Iterable<LexemSequence> {

  private ConcatableIterator<Lexem> lexemIterator;

  private final LexemSequenceInternalIterator lexemSequenceInternalIterator = new LexemSequenceInternalIterator();

  @Getter
  private int lineCount;

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
      lineCount = Math.max(lineCount, lexem.getLineNumber());
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
