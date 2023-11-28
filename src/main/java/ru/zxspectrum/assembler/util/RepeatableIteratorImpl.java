package ru.zxspectrum.assembler.util;

import lombok.NonNull;

import java.util.Iterator;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
public class RepeatableIteratorImpl<E> implements RepeatableIterator<E> {
    private E current;

    private Iterator<E> iterator;

    public RepeatableIteratorImpl(@NonNull E... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("args is null or empty");
        }
        this.iterator = List.of(args).iterator();
    }

    public RepeatableIteratorImpl(@NonNull List<E> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("list is null or empty");
        }
        this.iterator = list.iterator();
    }

    public RepeatableIteratorImpl(@NonNull Iterator<E> iterator) {
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
