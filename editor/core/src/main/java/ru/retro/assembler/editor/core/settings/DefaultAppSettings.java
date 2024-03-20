package ru.retro.assembler.editor.core.settings;

import ru.retro.assembler.editor.core.util.AppSettingsFactory;

/**
 * DefaultSettings.
 *
 * @author Maxim Gorin
 */
public final class DefaultAppSettings {

  private DefaultAppSettings() {

  }

  public static AppSettingsFactory defaultAppSettingsFactory() {
    return new AppSettingsFactory() {
      @Override
      public AppSettings newAppSettings() {
        return new AppSettings();
      }
    };
  }

}
