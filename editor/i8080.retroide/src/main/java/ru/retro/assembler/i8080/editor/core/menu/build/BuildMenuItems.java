package ru.retro.assembler.i8080.editor.core.menu.build;

import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Arrays;

public final class BuildMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return controller -> Arrays.asList(new CompileMenuItem(controller), new CompileRkmMenuItem(controller)
               , new CompileWavMenuItem(controller));
    }
}
