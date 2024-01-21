package ru.assembler.core.util;

import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public interface RepeatableIterator<E> extends Iterator<E> {
    E current();
}
