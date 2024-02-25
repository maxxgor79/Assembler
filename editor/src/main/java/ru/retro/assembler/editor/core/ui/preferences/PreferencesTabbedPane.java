package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class PreferencesTabbedPane extends JTabbedPane {
    @Getter
    private PreferencesPanel preferencesPanel;

    public PreferencesTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        preferencesPanel = new PreferencesPanel();
        addTab(Messages.get(Messages.COMPILER), preferencesPanel);
    }
}
