package ru.retro.assembler.editor.core.ui.components;

import java.util.Collection;

public interface Menu {
    void add(MenuItem item);

    void addAll(Collection<MenuItem> items);

    Collection<MenuItem> enumerate();

    void update(MenuItem item);

    void updateAll();
}
