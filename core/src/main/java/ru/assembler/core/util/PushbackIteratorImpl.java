package ru.assembler.core.util;

import lombok.NonNull;

import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public class PushbackIteratorImpl<E> implements PushbackIterator<E> {
    private final Iterator<E> iterator;

    public PushbackIteratorImpl(@NonNull Iterator<E> iterator) {
        this.iterator = iterator;
    }

    private E rollbackElement;

    private E lastElement;

    @Override
    public boolean back() {
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
