package ru.zxspectrum.assembler.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxim Gorin
 */
public final class Lists {
    private Lists() {
    }

    public static <E> int contains(List<E> l1, List<E> l2) {
        return contains(l1, 0, l2);
    }

    public static <E> int contains(List<E> l1, int offset, List<E> l2) {
        if (l1 == null || l2 == null) {
            return -1;
        }
        if (offset < 0 || offset >= l1.size()) {
            throw new IllegalArgumentException("offset is out of range");
        }
        if (l1.isEmpty() || l2.isEmpty()) {
            return -1;
        }
        while (true) {
            if (l2.size() > (l1.size() - offset)) {
                return -1;
            }
            int i = 0;
            while (i < l2.size()) {
                if (!l1.get(i + offset).equals(l2.get(i))) {
                    break;
                }
                i++;
            }
            if (i == l2.size()) {
                return offset;
            }
            offset++;
        }
    }

    public static <E> List<E> copy(List<E> l1, int fromIndex) {
        if (l1 == null) {
            throw new NullPointerException("l1");
        }
        if (l1.isEmpty()) {
            return Collections.emptyList();
        }
        if (fromIndex < 0 || fromIndex >= l1.size()) {
            throw new IllegalArgumentException("fromIndex is out of range");
        }
        List<E> newList = new ArrayList<>(l1.size() - fromIndex);
        for (int i = fromIndex; i < l1.size(); i++) {
            newList.add(l1.get(i));
        }
        return newList;
    }
}