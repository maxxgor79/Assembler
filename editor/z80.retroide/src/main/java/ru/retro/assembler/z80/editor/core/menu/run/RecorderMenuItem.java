package ru.retro.assembler.z80.editor.core.menu.run;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.MainWindow;
import ru.retro.assembler.editor.core.ui.OpenWavFileChooser;
import ru.retro.assembler.editor.core.ui.menu.AbstractMenuItem;
import ru.retro.assembler.editor.core.ui.record.AudioRecorder;
import ru.retro.assembler.editor.core.util.ResourceUtils;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;
import ru.retro.assembler.z80.editor.core.settings.Z80AppSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * RecorderMenuItem.
 *
 * @author Maxim Gorin
 */
@Slf4j
public class RecorderMenuItem extends AbstractMenuItem {

    private MainWindow mainWindow;

    private OpenWavFileChooser fileChooser;

    private AudioRecorder recorder;

    private Z80AppSettings settings;

    public RecorderMenuItem(@NonNull Controller controller) {
        super(controller, Z80Messages.getInstance().get(Z80Messages.RECORDER), (char) 0, KeyStroke.getKeyStroke(KeyEvent
                .VK_F11, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "/icon16x16/cassette.png");
        mainWindow = controller.getMainWindow();
        settings = (Z80AppSettings) controller.getSettings();
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
            recorder.getInteractivePanel().setBkColor(Color.WHITE);
            recorder.getInteractivePanel().setWaveColor(Color.RED);
            recorder.getInteractivePanel().setLineColor(Color.BLACK);
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
            ((Z80AppSettings) controller.getSettings()).setWavOpenDialogCurrentDirectory(fileChooser
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
