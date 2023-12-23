package ru.zxspectrum.assembler.util;

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
        if (lastItem != null && externalHasNext()) {
            return true;
        }
        return false;
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
