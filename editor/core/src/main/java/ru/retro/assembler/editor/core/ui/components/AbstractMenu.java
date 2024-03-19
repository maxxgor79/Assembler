package ru.retro.assembler.editor.core.ui.components;

import lombok.NonNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMenu implements Menu {
    private final List<MenuItem> menuItems = new LinkedList<>();

    private final Map<MenuItem, JMenuItem> menuItemMap = new HashMap<>();

    private JMenu jMenu;

    protected AbstractMenu(JMenu jMenu) {
        this.jMenu = jMenu;
    }

    @Override
    public void add(@NonNull MenuItem item) {
        final JMenuItem jMenuItem = new JMenuItem(item.name());
        if (item.icon() != null) {
            jMenuItem.setIcon(item.icon());
        }
        if (item.mnemonic() != 0) {
            jMenuItem.setMnemonic(item.mnemonic());
        }
        if (item.keystroke() != null) {
            jMenuItem.setAccelerator(item.keystroke());
        }
        jMenuItem.addActionListener(e -> {
            item.onAction(e);
        });
        jMenu.add(jMenuItem);
        if (item.hasSeparator()) {
            jMenu.addSeparator();
        }
        menuItems.add(item);
        menuItemMap.put(item, jMenuItem);
    }

    @Override
    public void addAll(@NonNull Collection<MenuItem> items) {
        for (MenuItem item : items) {
            add(item);
        }
    }

    @Override
    public Collection<MenuItem> enumerate() {
        return Collections.unmodifiableCollection(menuItems);
    }

    @Override
    public void update(@NonNull MenuItem item) {
        final JMenuItem jMenuItem = menuItemMap.get(item);
        if (jMenuItem == null) {
            return;
        }
        jMenuItem.setEnabled(item.isEnabled());
    }

    @Override
    public void updateAll() {
        for (MenuItem item : enumerate()) {
            update(item);
        }
    }
}
