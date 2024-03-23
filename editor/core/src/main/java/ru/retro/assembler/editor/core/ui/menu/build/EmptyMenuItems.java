package ru.retro.assembler.editor.core.ui.menu.build;

import java.util.Collection;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Collections;

public final class EmptyMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return new MenuItemFactory() {
            @Override
            public Collection<MenuItem> newBuildMenuItems(Controller controller) {
                return Collections.emptyList();
            }

            @Override
            public Collection<MenuItem> newRunMenuItems(Controller controller) {
                return Collections.emptyList();
            }
        };
    }
}
