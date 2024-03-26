package ru.retro.assembler.z80.editor.core.settings;

import lombok.Getter;
import lombok.Setter;
import ru.retro.assembler.editor.core.access.Setting;
import ru.retro.assembler.editor.core.settings.AppSettings;

/**
 * Z80AppSettings.
 *
 * @author Maxim Gorin
 */
public class Z80AppSettings extends AppSettings {
  @Setting
  @Getter
  @Setter
  private String wavOpenDialogCurrentDirectory = "${user.home}";

  public Z80AppSettings() {
    setOutputDirectory("${user.home}${file.separator}z80${file.separator}output");
    setCompilerPath("z80asm");
  }
}
