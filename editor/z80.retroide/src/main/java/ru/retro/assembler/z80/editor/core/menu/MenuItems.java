package ru.retro.assembler.z80.editor.core.menu;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.MenuItemFactory;

import java.util.Arrays;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.z80.editor.core.menu.build.CompileMenuItem;
import ru.retro.assembler.z80.editor.core.menu.build.CompileTZXMenuItem;
import ru.retro.assembler.z80.editor.core.menu.build.CompileTapMenuItem;
import ru.retro.assembler.z80.editor.core.menu.build.CompileWavMenuItem;
import ru.retro.assembler.z80.editor.core.menu.run.RecorderMenuItem;
import ru.retro.assembler.z80.editor.core.menu.run.TapePlayerMenuItem;

public final class MenuItems {

  public static MenuItemFactory defaultMenuItemFactory() {
    return new MenuItemFactory() {
      @Override
      public Collection<MenuItem> newBuildMenuItems(@NonNull final Controller controller) {
        final List<MenuItem> list = Arrays.asList(new CompileMenuItem(controller)
            , new CompileTapMenuItem(controller), new CompileTZXMenuItem(controller)
            , new CompileWavMenuItem(controller));
        UIUtils.sort(list);
        return list;
      }

      @Override
      public Collection<MenuItem> newRunMenuItems(@NonNull final Controller controller) {
        final List<MenuItem> list = Arrays.asList(new TapePlayerMenuItem(controller)
            , new RecorderMenuItem(controller));
        UIUtils.sort(list);
        return list;
      }
    };
  }
}
