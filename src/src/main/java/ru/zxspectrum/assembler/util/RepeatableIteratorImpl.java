package ru.zxspectrum.assembler.util;

import java.util.Iterator;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
public class RepeatableIteratorImpl<E> implements RepeatableIterator<E> {
    private E current;

    private Iterator<E> iterator;

    public RepeatableIteratorImpl(E... args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("args is null or empty");
        }
        this.iterator = List.of(args).iterator();
    }

    public RepeatableIteratorImpl(List<E> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list is null or empty");
        }
        this.iterator = list.iterator();
    }

    public RepeatableIteratorImpl(Iterator<E> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator is null");
        }
        this.iterator = iterator;
    }

    @Override
    public E current() {
        return current;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return current = iterator.next();
    }
}
