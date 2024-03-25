package ru.retro.assembler.i8080.editor.core.menu.build;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.util.ToolButtonFactory;

import java.util.Arrays;
import ru.retro.assembler.editor.core.util.UIUtils;

public final class BuildToolButtons {

  private BuildToolButtons() {

  }

  public static ToolButtonFactory defaultToolButtonFactory() {
    return controller -> {
      final List<ToolButton> list = Arrays.asList(new BuildToolButton(controller));
      UIUtils.sort(list);
      return list;
    };
  }
}
