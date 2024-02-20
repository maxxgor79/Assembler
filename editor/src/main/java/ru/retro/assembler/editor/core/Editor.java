package ru.retro.assembler.editor.core;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import ru.retro.assembler.editor.core.ui.MainWindow;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class Editor implements MouseWheelListener {
    private static final String SETTINGS_FILENAME = "setting.properties";

    public static void main(String []args) {
        MainWindow windows = new MainWindow();
        windows.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> {
            windows.setVisible(true);
        });
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
