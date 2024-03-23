package ru.retro.assembler.z80.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@Slf4j
public class CompileMenuItem extends AbstractCompileMenuItem {
    public CompileMenuItem(@NonNull Controller controller) {
        super(controller, Z80Messages.getInstance().get(Z80Messages.COMPILE), 'C', KeyStroke.getKeyStroke(KeyEvent
                        .VK_F9, InputEvent.CTRL_DOWN_MASK), "/icon16x16/equipment.png");
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean hasSeparator() {
        return true;
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
