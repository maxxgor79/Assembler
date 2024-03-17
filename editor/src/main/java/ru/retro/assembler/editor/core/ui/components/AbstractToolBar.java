package ru.retro.assembler.editor.core.ui.components;

import lombok.NonNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractToolBar implements ToolBar {
    private final List<ToolButton> buttons = new LinkedList<>();

    private final Map<ToolButton, JButton> toolButtonMap = new HashMap<>();

    private JToolBar jToolBar;

    public AbstractToolBar(@NonNull JToolBar jToolBar) {
        this.jToolBar = jToolBar;
    }

    @Override
    public void add(@NonNull ToolButton button) {
        final JButton jButton = new JButton();
        if (button.size() != null) {
            jButton.setSize(button.size());
            jButton.setPreferredSize(button.size());
        }
        if (button.icon() != null) {
            jButton.setIcon(button.icon());
        }
        jButton.setToolTipText(button.hint());
        jButton.addActionListener(e -> {
            button.onAction(e);
        });
        if (button.hasSeparator()) {
            jToolBar.addSeparator();
        }
        jToolBar.add(jButton);
        buttons.add(button);
        toolButtonMap.put(button, jButton);
    }

    @Override
    public void addAll(@NonNull Collection<ToolButton> buttons) {
        for (ToolButton button : buttons) {
            add(button);
        }
    }

    @Override
    public Collection<ToolButton> enumerate() {
        return Collections.unmodifiableCollection(buttons);
    }

    public void update(@NonNull ToolButton button) {
        JButton jButton = toolButtonMap.get(button);
        if (jButton == null) {
            return;
        }
        jButton.setEnabled(button.isEnabled());
    }

    @Override
    public void updateAll() {
        for (ToolButton button : enumerate()) {
            update(button);
        }
    }
}
