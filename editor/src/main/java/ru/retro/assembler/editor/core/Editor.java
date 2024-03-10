package ru.retro.assembler.editor.core;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.util.UIUtils;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public class Editor {
    private static Controller controller;

    public static void main(String[] args) {
        setSystemVariables();
        setAppName();
        controller = new Controller(List.of(args));
        SwingUtilities.invokeLater(controller);
    }

    private static void setSystemVariables() {
        //System.setProperty("z80asm.embedded", "true");
    }

    private static void setAppName() {
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", Messages.get(Messages.CAPTION));
        }
    }
}
