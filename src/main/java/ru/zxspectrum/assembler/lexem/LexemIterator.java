package ru.zxspectrum.assembler.lexem;

import ru.zxspectrum.assembler.util.RepeatableIteratorImpl;

import java.util.Iterator;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public class LexemIterator extends RepeatableIteratorImpl<Lexem> {
    public LexemIterator(Lexem... args) {
        super(args);
    }

    public LexemIterator(List<Lexem> list) {
        super(list);
    }

    public LexemIterator(Iterator<Lexem> iterator) {
        super(iterator);
    }
}
