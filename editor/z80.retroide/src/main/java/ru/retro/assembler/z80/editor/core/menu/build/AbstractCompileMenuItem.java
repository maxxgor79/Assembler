package ru.retro.assembler.z80.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.menu.AbstractMenuItem;
import ru.retro.assembler.z80.editor.core.compile.EmbeddedCompiling;
import ru.retro.assembler.z80.editor.core.compile.ExternalCompiling;
import ru.retro.assembler.z80.editor.utils.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

@Slf4j
abstract class AbstractCompileMenuItem extends AbstractMenuItem {
    protected AbstractCompileMenuItem(@NonNull Controller controller, @NonNull String text, char mnmc
            , KeyStroke keyStroke, String iconPath) {
        super(controller, text, mnmc, keyStroke, null);
        if (iconPath != null) {
            try {
                this.icon = ResourceUtils.loadIcon(iconPath);
            } catch (IOException var7) {
                log.error(var7.getMessage(), var7);
            }
        }
    }

    protected void compile(@NonNull final Source src, String... args) {
        controller.cleanConsole();
        if (!controller.getSettings().isCompiledEmbedded()) {
            final ExternalCompiling compiling = new ExternalCompiling(controller.getSettings()
                    , controller.getMainWindow());
            compiling.compile(src, args);
        } else {
            final EmbeddedCompiling compiling = new EmbeddedCompiling(controller.getSettings()
                    , controller.getMainWindow());
            compiling.compile(src, args);
        }
    }

    @Override
    public boolean isEnabled() {
        final Source selectedSource = controller.getMainWindow().getSourceTabbedPane().getSourceSelected();
        if (selectedSource != null) {
            String text = selectedSource.getTextArea().getText();
            return text != null && !text.trim().isEmpty() && controller.getMainWindow().getSourceTabbedPane()
                    .getTabCount() != 0;
        }
        return controller.getMainWindow().getSourceTabbedPane().getTabCount() != 0;
    }
}
