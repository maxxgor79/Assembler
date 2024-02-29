package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.env.Environment;
import ru.retro.assembler.editor.core.io.Source;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Maxim Gorin
 * Date: 29.02.2024
 */
@Slf4j
public class Activator implements ActionListener {
    private final MainWindow mainWindow;

    public Activator(@NonNull final MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    private void run() {
        boolean noSources = mainWindow.getSourceTabbedPane().getTabCount() == 0;
        mainWindow.getBtnSave().setEnabled(!noSources);
        mainWindow.getBtnReload().setEnabled(!noSources);
        mainWindow.getBtnCompile().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiSave().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiSaveAs().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiSaveAll().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiReloadAllFromDisk().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiClose().setEnabled(!noSources);
        mainWindow.getFileMenuItems().getMiCloseAll().setEnabled(!noSources);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSaveAs().setEnabled(!noSources);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSave().setEnabled(!noSources);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiClose().setEnabled(!noSources);
        if (noSources) {
            mainWindow.getEditMenuItems().getMiUndo().setEnabled(false);
            mainWindow.getEditMenuItems().getMiCopy().setEnabled(false);
            mainWindow.getEditMenuItems().getMiCut().setEnabled(false);
            mainWindow.getEditMenuItems().getMiPaste().setEnabled(false);
            mainWindow.getEditMenuItems().getMiFind().setEnabled(false);
            mainWindow.getEditMenuItems().getMiFindNext().setEnabled(false);
            mainWindow.getConsole().getConsolePopupMenu().getMiCopyText().setEnabled(false);
        }
        mainWindow.getBuildMenuItems().getMiCompile().setEnabled(!noSources);
        mainWindow.getBuildMenuItems().getMiCompileTap().setEnabled(!noSources);
        mainWindow.getBuildMenuItems().getMiCompileTzx().setEnabled(!noSources);
        mainWindow.getBuildMenuItems().getMiCompileWav().setEnabled(!noSources);
        if (!noSources) {
            final Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
            mainWindow.getEditMenuItems().getMiUndo().setEnabled(src.getTextArea().canUndo());
            final String selectedText = src.getTextArea().getSelectedText();
            mainWindow.getEditMenuItems().getMiCut().setEnabled(selectedText != null);
            mainWindow.getEditMenuItems().getMiCopy().setEnabled(selectedText != null);
            mainWindow.getEditMenuItems().getMiFind().setEnabled(!src.getTextArea().getText().isEmpty());
            mainWindow.getEditMenuItems().getMiFindNext().setEnabled(Environment.getInstance().getNextOccurrenceIndex()
                    != -1);
            try {
                mainWindow.getEditMenuItems().getMiPaste().setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard()
                        .getData(DataFlavor.getTextPlainUnicodeFlavor()) != null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            mainWindow.getConsole().getConsolePopupMenu().getMiCopyText().setEnabled(mainWindow.getConsole()
                    .getArea().getSelectedText() != null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
    }
}
