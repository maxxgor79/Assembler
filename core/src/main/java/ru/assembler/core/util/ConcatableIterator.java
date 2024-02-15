package ru.assembler.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import lombok.NonNull;

/**
 * ConcatableIterator.
 *
 * @author Maxim Gorin
 */
public class ConcatableIterator<E> implements Iterator<E> {

  private final LinkedList<Iterator<E>> iterators = new LinkedList<>();

  private Iterator<E> current;

  public ConcatableIterator(@NonNull Iterator<E> iter) {
    this(Arrays.asList(iter));
  }

  public ConcatableIterator(@NonNull Collection<Iterator<E>> col) {
    this.iterators.addAll(col);
    current = iterators.peekFirst();
    if (current != null) {
      iterators.removeFirst();
    }
  }

  @Override
  public boolean hasNext() {
    if (current == null) {
      return false;
    }
    if (current.hasNext()) {
      return true;
    }
    current = iterators.peekFirst();
    if (current != null) {
      iterators.removeFirst();
      return hasNext();
    }
    return false;
  }

  @Override
  public E next() {
    if (current == null) {
      throw new NoSuchElementException();
    }
    try {
      return current.next();
    } catch (NoSuchElementException e) {
    }
    current = iterators.peekFirst();
    if (current != null) {
      iterators.removeFirst();
    }
    return next();
  }

  public void concat(@NonNull Iterator<E> iter) {
    iterators.add(iter);
  }
}
