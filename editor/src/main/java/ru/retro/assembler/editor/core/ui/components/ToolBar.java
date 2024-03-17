package ru.retro.assembler.editor.core.ui.components;

import java.util.Collection;

public interface ToolBar {
    void add(ToolButton button);

    void addAll(Collection<ToolButton> buttons);

    Collection<ToolButton> enumerate();

    void update(ToolButton button);

    void updateAll();
}
