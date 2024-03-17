package ru.retro.assembler.editor.core.util;

import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;

import java.util.Collection;

public interface MenuItemFactory {
    Collection<MenuItem> newMenuItems(Controller controller);
}
