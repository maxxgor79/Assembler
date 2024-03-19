package ru.retro.assembler.editor.core.ui.menu.build;

import ru.retro.assembler.editor.core.util.ToolButtonFactory;

import java.util.Arrays;
import java.util.Collections;

public final class EmptyToolButtons {
    private EmptyToolButtons() {

    }

    public static ToolButtonFactory defaultToolButtonFactory() {
        return controller -> Collections.emptyList();
    }
}
