package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import ru.retro.assembler.editor.core.util.UIFactory;

/**
 * @Author: Maxim Gorin
 * Date: 18.03.2024
 */
public final class UIComponents {
    private UIComponents() {

    }

    public static UIFactory defaultUIFactory() {
        return new UIFactory() {
            @Override
            public ModalDialog newAboutDialog(@NonNull final Controller controller) {
                final AboutDialog dialog = new AboutDialog(controller.getMainWindow());
                dialog.setMajorVersion(controller.getSettings().getMajorVersion());
                dialog.setMinorVersion(controller.getSettings().getMinorVersion());
                dialog.setBuildVersion(controller.getBuildVersionReader().getBuildVersion());
                dialog.setBuildDate(controller.getBuildVersionReader().getBuildDate());
                return dialog;
            }
        };
    }
}
