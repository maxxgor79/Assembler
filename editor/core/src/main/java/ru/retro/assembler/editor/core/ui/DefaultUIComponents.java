package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.editor.core.util.UIFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
@Slf4j
public final class DefaultUIComponents {
    private DefaultUIComponents() {

    }

    public static UIFactory defaultUIFactory() {
        return new UIFactory() {
            @Override
            public ModalDialog newAboutDialog(@NonNull final Controller controller) {
                return null;
            }

            @Override
            public Image newTaskBarImage() {
                try {
                    final Image image = ResourceUtils.loadImage("/icon48x48/microchip.png");
                    return image;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Image newAboutDialogImage() {
                try {
                    final Image image = ResourceUtils.loadImage("/icon16x16/chip.png");
                    return image;
                } catch (IOException | UnsupportedOperationException | SecurityException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Collection<MenuItem> newBuildMenuItems(@NonNull Controller controller) {
                return Collections.emptyList();
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(@NonNull Controller controller) {
                return Collections.emptyList();
            }

            @Override
            public Collection<ToolButton> newToolButtons(@NonNull Controller controller) {
                return Collections.emptyList();
            }

            @Override
            public JFileChooser newOpenChooser() {
                return null;
            }

            @Override
            public JFileChooser newSaveChooser() {
                return null;
            }

            @Override
            public JFileChooser newImportChooser() {
                return null;
            }
        };
    }
}
