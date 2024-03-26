package ru.retro.assembler.i8080.editor.core.settings;

import lombok.Getter;
import lombok.Setter;
import ru.retro.assembler.editor.core.access.Setting;
import ru.retro.assembler.editor.core.settings.AppSettings;

/**
 * I8080AppSettings.
 *
 * @author Maxim Gorin
 */
public class I8080AppSettings extends AppSettings {
  @Setting
  @Getter
  @Setter
  private String wavOpenDialogCurrentDirectory = "${user.home}";

  public I8080AppSettings() {
    setOutputDirectory("${user.home}${file.separator}i8080${file.separator}output");
    setCompilerPath("mscasm");
  }

}
