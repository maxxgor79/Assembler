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
    private CompilerPanel compilerPanel;

    @Getter
    private OtherPanel otherPanel;

    public PreferencesTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        compilerPanel = new CompilerPanel();
        addTab(Messages.get(Messages.COMPILER), compilerPanel);
        otherPanel = new OtherPanel();
        addTab(Messages.get(Messages.OTHER), otherPanel);
    }
}
