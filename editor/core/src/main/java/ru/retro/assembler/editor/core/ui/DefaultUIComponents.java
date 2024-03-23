package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.editor.core.util.UIFactory;

import java.awt.*;
import java.io.IOException;

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
            public Image newWindowImage() {
                try {
                    final Image image = ResourceUtils.loadImage("/icon16x16/chip.png");
                    return image;
                } catch (IOException | UnsupportedOperationException | SecurityException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        };
    }
}
