package ru.retro.assembler.editor.core.util;

import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.ModalDialog;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
public interface UIFactory {
    ModalDialog newAboutDialog(Controller controller);
}
