package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
public class ToolsMenuItems {
    @Getter
    private JMenuItem miPreferences;

    public ToolsMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miPreferences);
    }

    private void initComponents() {
        miPreferences = new JMenuItem("Preferences");
        miPreferences.setMnemonic('P');

    }
}
