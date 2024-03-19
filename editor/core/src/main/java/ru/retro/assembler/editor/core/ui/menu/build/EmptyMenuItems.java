package ru.retro.assembler.editor.core.ui.menu.build;

import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Arrays;
import java.util.Collections;

public final class EmptyMenuItems {
    public static MenuItemFactory defaultMenuItemFactory() {
        return controller -> Collections.emptyList();
    }
}
