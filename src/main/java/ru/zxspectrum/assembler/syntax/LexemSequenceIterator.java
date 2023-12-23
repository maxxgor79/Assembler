package ru.zxspectrum.assembler.syntax;

import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.util.Iterator;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public class LexemSequenceIterator extends RepeatableIteratorImpl<LexemSequence> {
    public LexemSequenceIterator(LexemSequence... args) {
        super(args);
    }

    public LexemSequenceIterator(List<LexemSequence> list) {
        super(list);
    }

    public LexemSequenceIterator(Iterator<LexemSequence> iterator) {
        super(iterator);
    }
}
