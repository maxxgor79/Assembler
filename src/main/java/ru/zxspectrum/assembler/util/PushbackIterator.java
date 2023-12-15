package ru.zxspectrum.assembler.util;

/**
 * @Author Maxim Gorin
 */
public interface PushbackIterator<E> extends RepeatableIterator<E> {
    public boolean back();
}
