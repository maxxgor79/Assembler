package ru.zxspectrum.assembler.util;

import lombok.NonNull;

import java.util.Iterator;
import java.util.List;

/**
 * @Author Maxim Gorin
 */
public class IndexedArrayIterator<E> implements Iterator {
    private List<E> list;

    private int index;

    public IndexedArrayIterator(@NonNull List<E> list, int index) {
        this.list = list;
        if (index < 0 || index >= list.size()) {
            throw new ArrayIndexOutOfBoundsException("index");
        }
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            return null;
        }
        return list.get(index++);
    }
}
