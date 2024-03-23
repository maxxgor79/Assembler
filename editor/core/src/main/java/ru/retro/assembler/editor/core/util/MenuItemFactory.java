package ru.retro.assembler.editor.core.util;

import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;

import java.util.Collection;

public interface MenuItemFactory {
    Collection<MenuItem> newBuildMenuItems(Controller controller);

    Collection<MenuItem> newRunMenuItems(Controller controller);
}
