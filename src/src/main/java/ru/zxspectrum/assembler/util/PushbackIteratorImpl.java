package ru.zxspectrum.assembler.util;

import java.util.Iterator;

/**
 * @Author Maxim Gorin
 */
public class PushbackIteratorImpl<E> implements PushbackIterator<E> {
    private Iterator<E> iterator;

    public PushbackIteratorImpl(Iterator<E> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator is null");
        }
        this.iterator = iterator;
    }

    private E rollbackElement;

    private E lastElement;

    @Override
    public boolean rollback() {
        if (lastElement != null) {
            rollbackElement = lastElement;
            lastElement = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        if (rollbackElement != null) {
            return true;
        }
        return iterator.hasNext();
    }

    @Override
    public E next() {
        if (rollbackElement != null) {
            E element = rollbackElement;
            rollbackElement = null;
            return lastElement = element;
        }
        return lastElement = iterator.next();
    }

    @Override
    public E current() {
        return lastElement;
    }
}
