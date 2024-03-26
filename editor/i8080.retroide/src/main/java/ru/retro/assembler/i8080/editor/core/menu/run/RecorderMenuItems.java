package ru.retro.assembler.i8080.editor.core.menu.run;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.ui.OpenWavFileChooser;
import ru.retro.assembler.editor.core.ui.menu.AbstractMenuItem;
import ru.retro.assembler.editor.core.ui.record.AudioRecorder;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;
import ru.retro.assembler.i8080.editor.core.settings.I8080AppSettings;

/**
 * Recorder.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class RecorderMenuItems extends AbstractMenuItem {

  private MainWindow mainWindow;

  private OpenWavFileChooser fileChooser;

  private AudioRecorder recorder;

  private I8080AppSettings settings;

  public RecorderMenuItems(@NonNull Controller controller) {
    super(controller, I8080Messages.getInstance().get(I8080Messages.RECORDER), (char) 0
        , null, "/icon16x16/cassette.png");
    mainWindow = controller.getMainWindow();
    settings = (I8080AppSettings) controller.getSettings();
  }

  @Override
  public boolean hasSeparator() {
    return false;
  }

  @Override
  public void onAction(ActionEvent actionEvent) {
    log.info("Action record");
    record(actionEvent);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public int order() {
    return 2;
  }

  private AudioRecorder getRecorder() {
    if (recorder == null) {
      recorder = new AudioRecorder(mainWindow);
    }
    return recorder;
  }

  private OpenWavFileChooser getFileChooser() {
    if (fileChooser == null) {
      fileChooser = new OpenWavFileChooser();
    }
    fileChooser.setCurrentDirectory(new File(settings.getWavOpenDialogCurrentDirectory()));
    return fileChooser;
  }

  private void record(ActionEvent actionEvent) {
    final OpenWavFileChooser fileChooser = getFileChooser();
    if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
      settings.setWavOpenDialogCurrentDirectory(fileChooser
          .getCurrentDirectory().getAbsolutePath());
      final File selectedFile = fileChooser.getSelectedFile();
      if (selectedFile.exists()) {
        int option = JOptionPane.showConfirmDialog(mainWindow,
            Messages.getInstance().get(Messages.OVERWRITE_FILE)
            , Messages.getInstance().get(Messages.SAVE), JOptionPane.YES_NO_OPTION, JOptionPane
                .QUESTION_MESSAGE, ResourceUtils.getQuestionIcon());
        if (option == JOptionPane.NO_OPTION) {
          return;
        }
      }
      final AudioRecorder recorder = getRecorder();
      recorder.setFile(selectedFile);
      recorder.setLocationRelativeTo(mainWindow);
      recorder.showModal();
    }
  }
}
