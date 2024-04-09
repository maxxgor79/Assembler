package ru.retro.assembler.z80.editor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.z80.editor.core.imprt.FileImporters;
import ru.retro.assembler.z80.editor.core.settings.Z80AppSettings;
import ru.retro.assembler.z80.editor.core.ui.UIComponents;

@Slf4j
public final class Z80Editor extends Editor {
  @Getter
  private static final BuildVersionReader buildVersionReader = new BuildVersionReader();

  static {
    buildVersionReader.loadFromResource("/build.version");
  }

  private static void setDefaultExt() {
    log.info("Init extensions");
    UIUtils.putExt("asm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("z80", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("hasm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
    UIUtils.putExt("z80asm", SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_Z80);
  }

  private static void setDefaultFactories() {
    log.info("Init factories");
    Controller.setAppSettingsFactory(() -> new Z80AppSettings() {
      @Override
      public String getPrefix() {
        return "z80";
      }
    });
    Controller.setFileImporterFactory(FileImporters.defaultFileImporterFactory());
    Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
  }

  private static void printSystemInfo() {
    final String message = "Running on " + System.getProperty("os.name") + " version " + System
        .getProperty("os.version");
    log.info(message);
  }

  public static void main(String[] args) {
    printSystemInfo();
    setDefaultExt();
    setDefaultFactories();
    final Z80Editor editor = new Z80Editor();
    editor.entry(args);
  }
}
