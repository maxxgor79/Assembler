package ru.assembler.core.util;

/**
 * @author Maxim Gorin
 */
public interface PushbackIterator<E> extends RepeatableIterator<E> {
    boolean back();
}
