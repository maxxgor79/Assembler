package ru.retro.assembler.z80.editor.core.ui;

import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.ui.DefaultUIComponents;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.util.UIFactory;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.z80.editor.core.menu.build.*;
import ru.retro.assembler.z80.editor.core.menu.run.RecorderMenuItem;
import ru.retro.assembler.z80.editor.core.menu.run.TapePlayerMenuItem;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
public final class UIComponents {
    private UIComponents() {

    }

    public static UIFactory defaultUIFactory(@NonNull final BuildVersionReader buildVersionReader) {
        return new UIFactory() {
            private UIFactory defaultUiFactory = DefaultUIComponents.defaultUIFactory();

            @Override
            public ModalDialog newAboutDialog(@NonNull Controller controller) {
                final AboutDialog dialog = new AboutDialog(controller.getMainWindow());
                dialog.setMajorVersion(controller.getSettings().getMajorVersion());
                dialog.setMinorVersion(controller.getSettings().getMinorVersion());
                dialog.setBuildVersion(buildVersionReader.getBuildVersion());
                dialog.setBuildDate(buildVersionReader.getBuildDate());
                return dialog;
            }

            @Override
            public Image newTaskBarImage() {
                return defaultUiFactory.newTaskBarImage();
            }

            @Override
            public Image newAboutDialogImage() {
                return defaultUiFactory.newAboutDialogImage();
            }

            @Override
            public Collection<MenuItem> newBuildMenuItems(Controller controller) {
                final List<MenuItem> list = Arrays.asList(new CompileMenuItem(controller)
                        , new CompileTapMenuItem(controller), new CompileTZXMenuItem(controller)
                        , new CompileWavMenuItem(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(Controller controller) {
                final List<MenuItem> list = Arrays.asList(new TapePlayerMenuItem(controller)
                        , new RecorderMenuItem(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public Collection<ToolButton> newToolButtons(Controller controller) {
                final List<ToolButton> list = Arrays.asList(new BuildToolButton(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public JFileChooser newOpenChooser() {
                return new OpenFileChooser();
            }

            @Override
            public JFileChooser newSaveChooser() {
                return new LocalizedSaveAsChooser();
            }

            @Override
            public JFileChooser newImportChooser() {
                return new ImportFileChooser();
            }
        };
    }
}
