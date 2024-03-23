package ru.retro.assembler.editor.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.control.Controller;

import javax.swing.*;
import java.util.List;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public class Editor {
    private Controller controller;

    public void entry(String[] args) {
        setSystemVariables();
        setAppName();
        controller = new Controller(List.of(args));
        SwingUtilities.invokeLater(controller);
    }

    private void setSystemVariables() {
        //System.setProperty("z80asm.embedded", "true");
    }

    private void setAppName() {
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", Messages.getInstance().get(Messages.CAPTION));
        }
    }
}
