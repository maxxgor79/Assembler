package ru.retro.assembler.i8080.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;

import java.awt.*;
import java.awt.event.ActionEvent;

@Slf4j
public class BuildToolButton extends AbstractCompileMenuItem implements ToolButton {
    private Dimension size;

    private String hint;

    protected BuildToolButton(@NonNull Controller controller) {
        super(controller, "", (char) 0, null, "/icon32x32/compile.png");
        size = new Dimension(ICON_WIDTH, ICON_HEIGHT);
        hint = I8080Messages.getInstance().get(I8080Messages.COMPILE);
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public Dimension size() {
        return size;
    }

    @Override
    public String hint() {
        return hint;
    }

    @Override
    public boolean hasSeparator() {
        return false;
    }

    @Override
    public void onAction(ActionEvent e) {
        log.info("Action compile");
        compile();
    }

    private void compile() {
        final Source selectedSource = controller.getMainWindow().getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource);
    }
}
