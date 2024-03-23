package ru.retro.assembler.z80.editor.core.menu.build;

import java.util.Collection;
import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Arrays;
import ru.retro.assembler.z80.editor.core.menu.run.TapePlayerMenuItem;

public final class BuildMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return new MenuItemFactory() {
            @Override
            public Collection<MenuItem> newBuildMenuItems(@NonNull final Controller controller) {
                return Arrays.asList(new CompileMenuItem(controller), new CompileTapMenuItem(controller)
                    , new CompileTZXMenuItem(controller), new CompileWavMenuItem(controller));
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(@NonNull final Controller controller) {
                return Arrays.asList(new TapePlayerMenuItem(controller));
            }
        };
    }
}
