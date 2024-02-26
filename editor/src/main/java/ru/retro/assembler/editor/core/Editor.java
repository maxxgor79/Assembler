package ru.retro.assembler.editor.core;

import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.MainWindow;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public class Editor {

    private static Controller controller;

    public static void main(String[] args) {
        //Locale.setDefault(new Locale("ru"));
        controller = new Controller();
        SwingUtilities.invokeLater(controller);
    }
}
