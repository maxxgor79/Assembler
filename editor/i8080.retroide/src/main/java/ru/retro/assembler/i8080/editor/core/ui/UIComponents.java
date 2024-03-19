package ru.retro.assembler.i8080.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.util.UIFactory;
import ru.retro.assembler.i8080.editor.utils.ResourceUtils;

import java.awt.*;
import java.io.IOException;

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
            public ModalDialog newAboutDialog(Controller controller) {
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
            public Image newWindowImage() {
                try {
                    final Image image = ResourceUtils.loadImage("/icon16x16/keyboard.png");
                    return image;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        };
    }
}
