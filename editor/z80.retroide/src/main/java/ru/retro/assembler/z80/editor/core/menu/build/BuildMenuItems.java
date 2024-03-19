package ru.retro.assembler.z80.editor.core.menu.build;

import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Arrays;

public final class BuildMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return controller -> Arrays.asList(new CompileMenuItem(controller), new CompileTapMenuItem(controller)
                , new CompileTZXMenuItem(controller), new CompileWavMenuItem(controller));
    }
}
