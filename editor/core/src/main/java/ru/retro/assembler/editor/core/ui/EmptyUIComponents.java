package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import ru.retro.assembler.editor.core.util.UIFactory;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
public final class EmptyUIComponents {
    private EmptyUIComponents() {

    }

    public static UIFactory defaultUIFactory() {
        return new UIFactory() {
            @Override
            public ModalDialog newAboutDialog(@NonNull final Controller controller) {
                return null;
            }
        };
    }
}
