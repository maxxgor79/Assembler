package ru.assembler.core.util;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public abstract class AnalyzerIterator<E> implements Iterator {
    protected E lastItem;

    @Override
    public boolean hasNext() {
        if (lastItem == null) {
            lastItem = next();
        }
        return lastItem != null && externalHasNext();
    }

    @Override
    public E next() {
        E item;
        if (lastItem != null) {
            item = lastItem;
        } else {
            try {
                item = externalNext();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        lastItem = null;
        return item;
    }

    protected abstract E externalNext() throws IOException;

    protected abstract boolean externalHasNext();
}
