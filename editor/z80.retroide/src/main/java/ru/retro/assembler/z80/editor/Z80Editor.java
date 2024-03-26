package ru.retro.assembler.z80.editor;

import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.retro.assembler.editor.core.Editor;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.io.BuildVersionReader;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.record.AudioRecorder;
import ru.retro.assembler.editor.core.util.UIUtils;
import ru.retro.assembler.z80.editor.core.menu.build.BuildMenuItems;
import ru.retro.assembler.z80.editor.core.menu.build.BuildToolButtons;
import ru.retro.assembler.z80.editor.core.ui.FileChoosers;
import ru.retro.assembler.z80.editor.core.ui.UIComponents;
import ru.retro.assembler.z80.editor.utils.ResourceUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
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

  private static AppSettings createSettings() {
    final AppSettings instance = new AppSettings() {
      @Override
      public String getPrefix() {
        return "z80";
      }
    };
    instance.setOutputDirectory("${user.home}${file.separator}z80${file.separator}output");
    instance.setCompilerPath("z80asm");
    return instance;
  }

  private static void setDefaultFactories() {
    Controller.setToolButtonFactory(BuildToolButtons.defaultToolButtonFactory());
    Controller.setMenuItemFactory(BuildMenuItems.defaultMenuItemFactory());
    Controller.setFileChooserFactory(FileChoosers.defaultFileChooserFactory());
    Controller.setUiFactory(UIComponents.defaultUIFactory(buildVersionReader));
    Controller.setAppSettingsFactory(() -> createSettings());
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
    final Editor editor = new Editor();
    editor.entry(args);
  }
}
