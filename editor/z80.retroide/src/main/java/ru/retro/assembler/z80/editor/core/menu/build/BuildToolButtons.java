package ru.retro.assembler.z80.editor.core.menu.build;

import ru.retro.assembler.editor.core.util.ToolButtonFactory;

import java.util.Arrays;

public final class BuildToolButtons {
    private BuildToolButtons() {

    }

    public static ToolButtonFactory defaultToolButtonFactory() {
        return controller -> Arrays.asList(new BuildToolButton(controller));
    }
}
