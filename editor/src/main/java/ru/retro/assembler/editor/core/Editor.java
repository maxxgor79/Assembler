package ru.retro.assembler.editor.core;

import ru.retro.assembler.editor.core.ui.MainWindow;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class Editor {
    private static final String SETTINGS_FILENAME = "setting.properties";

    public static void main(String []args) {
        MainWindow windows = new MainWindow();
        SwingUtilities.invokeLater(() -> {
            windows.setVisible(true);
        });
    }
}
