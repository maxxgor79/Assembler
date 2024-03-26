package ru.retro.assembler.editor.core.ui;

import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.FileUtils;

/**
 * OpenWavFileChooser.
 *
 * @author Maxim Gorin
 */
public class OpenWavFileChooser extends LocalizedOpenChooser {

  public OpenWavFileChooser() {
    initComponent();
  }

  public OpenWavFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
    initComponent();
  }

  public OpenWavFileChooser(File currentDirectory) {
    super(currentDirectory);
    initComponent();
  }

  private void initComponent() {
    setDialogTitle(Messages.getInstance().get(Messages.OPEN_FILE));
    setMultiSelectionEnabled(true);
    FileNameExtensionFilter extension = new FileNameExtensionFilter(Messages.getInstance()
        .get(Messages.WAVE_FILES), "wav");
    addChoosableFileFilter(extension);
  }

  @Override
  public File getSelectedFile() {
    if (super.getSelectedFile() == null) {
      return null;
    }
    if (super.getFileFilter() instanceof FileNameExtensionFilter) {
      final FileNameExtensionFilter filter = (FileNameExtensionFilter) super.getFileFilter();
      if (filter.getExtensions() != null && (filter.getExtensions().length > 0)) {
        return FileUtils.addExt(super.getSelectedFile(), filter.getExtensions()[0]);
      }
    }
    return super.getSelectedFile();
  }
}
