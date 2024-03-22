package ru.retro.assembler.i8080.editor.core.menu.build;

import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.MenuItemFactory;
import ru.retro.assembler.i8080.editor.core.menu.run.TapePlayerMenuItem;

import java.util.Arrays;
import java.util.Collection;

public final class BuildMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return new MenuItemFactory() {
            @Override
            public Collection<MenuItem> newBuildMenuItems(Controller controller) {
                return Arrays.asList(new CompileMenuItem(controller), new CompileRkmMenuItem(controller)
                        , new CompileWavMenuItem(controller));
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(Controller controller) {
                return Arrays.asList(new TapePlayerMenuItem(controller));
            }
        };
    }
}
