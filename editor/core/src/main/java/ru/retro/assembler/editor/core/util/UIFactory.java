package ru.retro.assembler.editor.core.util;

import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.ui.components.ToolButton;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
public interface UIFactory {
    ModalDialog newAboutDialog(Controller controller);

    Image newTaskBarImage();

    Image newAboutDialogImage();

    Collection<MenuItem> newBuildMenuItems(Controller controller);

    Collection<MenuItem> newRunMenuItems(Controller controller);

    Collection<ToolButton> newToolButtons(Controller controller);

    JFileChooser newOpenChooser();

    JFileChooser newSaveChooser();

    JFileChooser newImportChooser();
}
