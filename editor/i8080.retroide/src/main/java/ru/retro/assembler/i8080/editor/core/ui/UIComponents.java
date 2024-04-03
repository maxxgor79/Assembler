package ru.retro.assembler.i8080.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.util.UIFactory;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.i8080.editor.core.menu.build.BuildToolButton;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileRkmMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileWavMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.run.RecorderMenuItems;
import ru.retro.assembler.i8080.editor.core.menu.run.TapePlayerMenuItem;
import ru.retro.assembler.i8080.editor.utils.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
@Slf4j
public final class UIComponents {
    private UIComponents() {

    }

    public static UIFactory defaultUIFactory(@NonNull final BuildVersionReader buildVersionReader) {
        return new UIFactory() {
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
                try {
                    final Image image = ResourceUtils.loadImage("/icon48x48/keyboard.png");
                    return image;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Image newAboutDialogImage() {
                try {
                    final Image image = ResourceUtils.loadImage("/icon16x16/keyboard.png");
                    return image;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Collection<MenuItem> newBuildMenuItems(Controller controller) {
                final List<MenuItem> list = Arrays.asList(new CompileMenuItem(controller)
                        , new CompileRkmMenuItem(controller), new CompileWavMenuItem(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(Controller controller) {
                final List<MenuItem> list =  Arrays.asList(new TapePlayerMenuItem(controller)
                        , new RecorderMenuItems(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public Collection<ToolButton> newToolButtons(@NonNull final Controller controller) {
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
                return new ImportFileDialog();
            }
        };
    }
}
