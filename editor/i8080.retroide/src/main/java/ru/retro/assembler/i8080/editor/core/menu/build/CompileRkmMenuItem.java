package ru.retro.assembler.i8080.editor.core.menu.build;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;
import ru.retro.assembler.i8080.editor.utils.CLIUtils;

import java.awt.event.ActionEvent;

/**
 * @Author: Maxim Gorin
 * Date: 19.03.2024
 */
@Slf4j
public class CompileRkmMenuItem extends AbstractCompileMenuItem {
    public CompileRkmMenuItem(@NonNull Controller controller) {
        super(controller, I8080Messages.getInstance().get(I8080Messages.COMPILE_RKM), (char) 0, null
                , null);
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean hasSeparator() {
        return false;
    }

    @Override
    public void onAction(ActionEvent actionEvent) {
        log.info("Action compile into wave format");
        final Source selectedSource = controller.getMainWindow().getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource, CLIUtils.ARG_RKM);
    }
}
