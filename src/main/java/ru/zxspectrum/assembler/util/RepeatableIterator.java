package ru.zxspectrum.assembler.util;

import java.util.Iterator;

/**
 * @author Maxim Gorin
 */
public interface RepeatableIterator<E> extends Iterator<E> {
    E current();
}
