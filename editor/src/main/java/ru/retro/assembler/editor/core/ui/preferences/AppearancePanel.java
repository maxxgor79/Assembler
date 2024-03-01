package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class AppearancePanel extends JPanel {
    @Getter
    private EditorAppearancePanel editorAppearancePanel;

    @Getter
    private ConsoleAppearancePanel consoleAppearancePanel;

    public AppearancePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(2,1));
        editorAppearancePanel = new EditorAppearancePanel();
        add(editorAppearancePanel);
        consoleAppearancePanel = new ConsoleAppearancePanel();
        add(consoleAppearancePanel);
    }
}
