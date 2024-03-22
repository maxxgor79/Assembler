package ru.retro.assembler.z80.editor;

import lombok.Getter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.z80.editor.core.menu.build.BuildMenuItems;
import ru.retro.assembler.z80.editor.core.menu.build.BuildToolButtons;
import ru.retro.assembler.z80.editor.core.ui.FileChoosers;
import ru.retro.assembler.z80.editor.core.ui.UIComponents;

public class Z80Editor {

  @Getter
  private static final BuildVersionReader buildVersionReader = new BuildVersionReader();

  static {
    buildVersionReader.loadFromResource("/build.version");
  }

  private static void setDefaultExt() {
    UIUtils.putExt("asm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("z80", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("hasm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("z80asm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
  }

  private static void setDefaultFactories() {
    Controller.setToolButtonFactory(BuildToolButtons.defaultToolButtonFactory());
    Controller.setMenuItemFactory(BuildMenuItems.defaultMenuItemFactory());
    Controller.setFileChooserFactory(FileChoosers.defaultFileChooserFactory());
    Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
    Controller.setAppSettingsFactory(() -> new AppSettings() {
      @Override
      public String getPrefix() {
        return "z80";
      }
    });
  }

  public static void main(String[] args) {
    setDefaultExt();
    setDefaultFactories();
    final Editor editor = new Editor();
    editor.entry(args);
  }
}
