package ru.retro.assembler.i8080.editor.core.menu;

import java.util.List;
import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.MenuItemFactory;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileRkmMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.build.CompileWavMenuItem;
import ru.retro.assembler.i8080.editor.core.menu.run.RecorderMenuItems;
import ru.retro.assembler.i8080.editor.core.menu.run.TapePlayerMenuItem;

import java.util.Arrays;
import java.util.Collection;

public final class MenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return new MenuItemFactory() {
            @Override
            public Collection<MenuItem> newBuildMenuItems(@NonNull Controller controller) {
                final List<MenuItem> list = Arrays.asList(new CompileMenuItem(controller)
                    , new CompileRkmMenuItem(controller), new CompileWavMenuItem(controller));
                UIUtils.sort(list);
                return list;
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(Controller controller) {
                final List<MenuItem> list =  Arrays.asList(new TapePlayerMenuItem(controller)
                    , new RecorderMenuItems(controller));
                UIUtils.sort(list);
                return list;
            }
        };
    }
}
