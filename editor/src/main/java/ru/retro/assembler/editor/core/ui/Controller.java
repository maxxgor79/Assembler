package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.ConsoleWriter;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.preferences.PreferencesDialog;
import ru.retro.assembler.editor.core.util.CLIUtils;
import ru.retro.assembler.editor.core.util.UIUtils;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: Maxim Gorin
 * Date: 26.02.2024
 */
@Slf4j
public final class Controller implements Runnable {
    protected static final int TIMER_DELAY = 250;

    protected static final String NEW_SOURCE_NAME = "noname.asm";

    protected static final String SETTINGS_FILENAME = "editor.properties";

    protected final AppSettings settings = new AppSettings();

    protected MainWindow mainWindow;

    private AboutDialog aboutDialog;

    private PreferencesDialog preferencesDialog;

    private URI helpUri;

    private Timer timer;

    private int index = -1;

    /*
       private final CaretListener caretListener = e -> {
       JTextArea textArea = (JTextArea) e.getSource();
       try {
           int line = UIUtils.getLine(textArea);
           int col = UIUtils.getColumn(textArea, line);
           statusPanel.setPosition(line + 1, col + 1);
       } catch (BadLocationException ex) {
           log.error(ex.getMessage(), ex);
       }
   };

     */
    public Controller() {
        this.mainWindow = new MainWindow();
        this.preferencesDialog = new PreferencesDialog(mainWindow);
        this.aboutDialog = new AboutDialog(mainWindow);
        loadSettings();
        initListeners();
    }

    @Override
    public void run() {
        mainWindow.setVisible(true);
    }

    private void loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILENAME)) {
            settings.load(fis);
            log.info("Setting are loaded successfully");
        } catch (ConfigurationException | IOException e) {
            log.error(e.getMessage(), e);
        }
        apply(settings);
    }

    private void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILENAME)) {
            store(settings);
            settings.save(fos);
            log.info("Setting are saved successfully");
        } catch (ConfigurationException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void apply(@NonNull final AppSettings settings) {
        mainWindow.apply(settings);
        try {
            helpUri = new URI(settings.getHelpUri());
        } catch (URISyntaxException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
        if (settings.getCompilerPath() != null) {
            preferencesDialog.getPreferencesTabbedPane().getCompilerPanel().getCompilerPathField()
                    .setText(settings.getCompilerPath());
        }
        if (settings.getOutputDirectory() != null) {
            preferencesDialog.getPreferencesTabbedPane().getCompilerPanel().getOutputPathField()
                    .setText(settings.getOutputDirectory());
        }
        aboutDialog.setMajorVersion(settings.getMajorVersion());
        aboutDialog.setMinorVersion(settings.getMinorVersion());
        if (settings.getEncoding() != null) {
            preferencesDialog.getPreferencesTabbedPane().getOtherPanel().getCbEncoding()
                    .setSelectedItem(settings.getEncoding().toUpperCase());
        }
    }

    protected void store(@NonNull final AppSettings settings) {
        mainWindow.store(settings);
        settings.setCompilerPath(preferencesDialog.getPreferencesTabbedPane().getCompilerPanel()
                .getCompilerPathField().getText());
        settings.setOutputDirectory(preferencesDialog.getPreferencesTabbedPane().getCompilerPanel()
                .getOutputPathField().getText());
        settings.setEncoding((String) preferencesDialog.getPreferencesTabbedPane().getOtherPanel().getCbEncoding()
                .getSelectedItem());
    }

    //------------------------------------------------------------------------------------------------------------------
    protected void initListeners() {
        mainWindow.addWindowListener(windowAdapter);
        mainWindow.getHelpMenuItems().getMiHelp().addActionListener(helpListener);
        mainWindow.getHelpMenuItems().getMiAbout().addActionListener(aboutListener);
        mainWindow.getToolsMenuItems().getMiPreferences().addActionListener(preferencesListener);
        mainWindow.getBtnNew().addActionListener(newListener);
        mainWindow.getBtnOpen().addActionListener(openListener);
        mainWindow.getBtnSave().addActionListener(saveListener);
        mainWindow.getBtnReload().addActionListener(reloadAllListener);
        mainWindow.getBtnCompile().addActionListener(compileListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getFileMenuItems().getMiNew().addActionListener(newListener);
        mainWindow.getFileMenuItems().getMiOpen().addActionListener(openListener);
        mainWindow.getFileMenuItems().getMiSave().addActionListener(saveListener);
        mainWindow.getFileMenuItems().getMiSaveAs().addActionListener(saveAsListener);
        mainWindow.getFileMenuItems().getMiSaveAll().addActionListener(saveAllListener);
        mainWindow.getFileMenuItems().getMiClose().addActionListener(closeListener);
        mainWindow.getFileMenuItems().getMiCloseAll().addActionListener(closeAllListener);
        mainWindow.getFileMenuItems().getMiReloadAllFromDisk().addActionListener(reloadAllListener);
        mainWindow.getFileMenuItems().getMiExit().addActionListener(exitListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getEditMenuItems().getMiUndo().addActionListener(undoListener);
        mainWindow.getEditMenuItems().getMiCopy().addActionListener(copyListener);
        mainWindow.getEditMenuItems().getMiCut().addActionListener(cutListener);
        mainWindow.getEditMenuItems().getMiPaste().addActionListener(pasteListener);
        mainWindow.getEditMenuItems().getMiFind().addActionListener(findListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiClose().addActionListener(closeListener);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSave().addActionListener(saveListener);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSaveAs().addActionListener(saveAsListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getBuildMenuItems().getMiCompile().addActionListener(compileListener);
        mainWindow.getBuildMenuItems().getMiCompileTap().addActionListener(compileTapListener);
        mainWindow.getBuildMenuItems().getMiCompileTzx().addActionListener(compileTzxListener);
        mainWindow.getBuildMenuItems().getMiCompileWav().addActionListener(compileWavListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getSourceTabbedPane().addChangeListener(tabbedPaneChangedListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getConsole().getConsolePopupMenu().getMiClean().addActionListener(cleanConsoleListener);
        timer = new Timer(TIMER_DELAY, new Activator(mainWindow));
        timer.start();
    }

    //------------------------------------------------------------------------------------------------------------------
    private void newSource() {
        Source newSrc = new Source(new File(NEW_SOURCE_NAME));
        mainWindow.getSourceTabbedPane().add(newSrc);
    }

    private void openSource() {
        OpenFileChooser openFileChooser;
        if (settings.getOpenDialogCurrentDirectory() == null) {
            openFileChooser = new OpenFileChooser();
        } else {
            openFileChooser = new OpenFileChooser(settings.getOpenDialogCurrentDirectory());
        }
        if (openFileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            settings.setOpenDialogCurrentDirectory(openFileChooser.getCurrentDirectory().getAbsolutePath());
            try {
                for (File file : openFileChooser.getSelectedFiles()) {
                    final Source src = new Source(file);
                    src.load(file, settings.getEncoding());
                    int tabIndex = mainWindow.getSourceTabbedPane().indexOf(src);
                    if (tabIndex == -1) {
                        mainWindow.getSourceTabbedPane().add(src);
                    } else {
                        final Source existedSource = mainWindow.getSourceTabbedPane().getSource(tabIndex);
                        existedSource.setContent(src.getContent());
                        mainWindow.getSourceTabbedPane().setSelected(existedSource);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.ENCODING_ERROR), Messages.get(Messages
                        .ERROR), JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                        , JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveSource() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        saveSource(selectedSource);
    }

    private void saveSource(@NonNull Source src) {
        if (!src.isNew() && !src.hasChanges()) {
            return;
        }
        if (src.isNew()) {
            saveSourceAs(src);
            return;
        }
        final File file = src.getFile();
        try {
            src.save(file, settings.getEncoding());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                    , JOptionPane.ERROR_MESSAGE);
        }
    }

    private void overwriteSave(@NonNull File file, @NonNull Source src) throws IOException {
        if (file.exists()) {
            int option = JOptionPane.showConfirmDialog(mainWindow, Messages.get(Messages.OVERWRITE_FILE)
                    , Messages.get(Messages.SAVE), JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.NO_OPTION) {
                return;
            }
        }
        src.save(file, settings.getEncoding());
    }

    private void saveSourceAs(@NonNull Source src) {
        SaveAsChooser saveAsChooser;
        if (settings.getSaveDialogCurrentDirectory() == null) {
            saveAsChooser = new SaveAsChooser();
        } else {
            saveAsChooser = new SaveAsChooser(settings.getSaveDialogCurrentDirectory());
        }
        saveAsChooser.setSelectedFile(src.getFile());
        if (saveAsChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            final File file = saveAsChooser.getSelectedFile();
            settings.setSaveDialogCurrentDirectory(saveAsChooser.getCurrentDirectory().getAbsolutePath());
            try {
                overwriteSave(file, src);
                src.rename(file);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                        , JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAllSources() {
        for (int i = 0; i < mainWindow.getSourceTabbedPane().getTabCount(); i++) {
            final Source src = mainWindow.getSourceTabbedPane().getSource(i);
            saveSource(src);
        }
    }

    private void reloadAll() {
        for (int i = 0; i < mainWindow.getSourceTabbedPane().getTabCount(); i++) {
            Source src = mainWindow.getSourceTabbedPane().getSource(i);
            if (src.isNew()) {
                continue;
            }
            try {
                if (src.hasChanges()) {
                    final String message = String.format(Messages.get(Messages.SOURCE_CONTAINS_CHANGES), src.getFile()
                            .getName());
                    final int option = JOptionPane.showConfirmDialog(mainWindow, message, Messages.get(Messages.SAVE)
                            , JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        src.save(settings.getEncoding());
                        continue;
                    }
                }
                src.load();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                        , JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void closeSource() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        closeSource(selectedSource);
    }

    private void closeSource(@NonNull final Source src) {
        if (src.isNew()) {
            final String message = String.format(Messages.get(Messages.SOURCE_IS_UNSAVED), src.getName());
            final int option = JOptionPane.showConfirmDialog(mainWindow, message, Messages.get(Messages.SAVE)
                    , JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveSourceAs(src);
            }
        } else if (src.hasChanges()) {
            final String message = String.format(Messages.get(Messages.SOURCE_CONTAINS_CHANGES), src.getName());
            final int option = JOptionPane.showConfirmDialog(mainWindow, message, Messages.get(Messages.SAVE)
                    , JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    src.save(settings.getEncoding());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                            , JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        src.getTextArea().removeCaretListener(caretListener);
        mainWindow.getSourceTabbedPane().close(src);
    }

    private void closeAll() {
        final int i = 0;
        while (mainWindow.getSourceTabbedPane().getTabCount() != 0) {
            Source src = mainWindow.getSourceTabbedPane().getSource(i);
            closeSource(src);
        }
    }

    private void closeWindow() {
        int option = JOptionPane.showConfirmDialog(mainWindow, Messages.get(Messages.CLOSE_APPLICATION)
                , Messages.get(Messages.CLOSE), JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            closeAll();
            mainWindow.dispose();
            timer.stop();
            saveSettings();
        }
    }

    private void compile() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource);
    }

    private void compileTap() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource, CLIUtils.ARG_TAP);
    }

    private void compileTzx() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource, CLIUtils.ARG_TZX);
    }

    private void compileWav() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        compile(selectedSource, CLIUtils.ARG_WAV);
    }


    private void compile(@NonNull final Source src, String... args) {
        final String asmDir = preferencesDialog.getPreferencesTabbedPane().getCompilerPanel().getCompilerPathField()
                .getText();
        final String outputDir = preferencesDialog.getPreferencesTabbedPane().getCompilerPanel().getOutputPathField()
                .getText();
        final File pathToAsm = new File(asmDir, CLIUtils.ASM_FILENAME);
        try {
            if (!pathToAsm.exists()) {
                throw new FileNotFoundException(pathToAsm.getAbsolutePath());
            }
            final java.util.List<String> argList = CLIUtils.toList(pathToAsm.getAbsolutePath(), CLIUtils.ARG_OUTPUT
                    , outputDir, src.getFile().getAbsolutePath(), args);
            final Process p = new ProcessBuilder(argList).start();
            final ConsoleWriter consoleWriter = new ConsoleWriter(p, mainWindow.getConsole().getArea());
            SwingUtilities.invokeLater(consoleWriter);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow, String.format(Messages.get(Messages.FILE_NOT_FOUND)
                    , pathToAsm.getAbsolutePath()), Messages.get(Messages.ERROR), JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow, Messages.get(Messages.IO_ERROR), Messages.get(Messages.ERROR)
                    , JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cleanConsole() {
        mainWindow.getConsole().getArea().setText(null);
    }

    private void undo() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().undoLastAction();
    }

    private void copyText() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().copy();
    }

    private void cutText() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().cut();
    }

    private void pasteText() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().paste();
    }

    private void setPosition(@NonNull JTextArea textArea) {
        try {
            int line = UIUtils.getLine(textArea);
            int col = UIUtils.getColumn(textArea, line);
            mainWindow.getStatusPanel().setPosition(line + 1, col + 1);
        } catch (BadLocationException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private final ActionListener preferencesListener = e -> {
        if (preferencesDialog.showModal() == PreferencesDialog.OPTION_SAVE) {
            final String encoding = (String) preferencesDialog.getPreferencesTabbedPane().getOtherPanel()
                    .getCbEncoding().getSelectedItem();
            mainWindow.getStatusPanel().setEncoding(encoding);
            settings.setEncoding(encoding);
        }
    };

    private final WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            closeWindow();
        }
    };

    private final ActionListener helpListener = e -> {
        try {
            Desktop.getDesktop().browse(helpUri);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    };

    private final ActionListener aboutListener = e -> {
        aboutDialog.showModal();
    };

    private final ActionListener newListener = e -> {
        log.info("New action");
        newSource();
    };

    private final ActionListener openListener = e -> {
        log.info("Open action");
        openSource();
    };

    private final ActionListener saveListener = e -> {
        log.info("Save action");
        saveSource();
    };

    private final ActionListener reloadAllListener = e -> {
        log.info("Reload all action");
        reloadAll();
    };

    private final ActionListener saveAsListener = e -> {
        log.info("Save as action");
        Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
        saveSourceAs(src);
        mainWindow.getSourceTabbedPane().update(src);
    };

    private final ActionListener saveAllListener = e -> {
        log.info("Save all action");
        saveAllSources();
    };

    private final ActionListener closeListener = e -> {
        log.info("Close action");
        closeSource();
    };

    private final ActionListener closeAllListener = e -> {
        log.info("Close all action");
        closeAll();
    };

    private final ActionListener exitListener = e -> {
        log.info("Exit action");
        closeWindow();
    };

    private final ActionListener undoListener = e -> {
        log.info("Undo action");
        undo();
    };

    private final ActionListener copyListener = e -> {
        log.info("Copy action");
        copyText();
    };

    private final ActionListener cutListener = e -> {
        log.info("Cut action");
        cutText();
    };

    private final ActionListener pasteListener = e -> {
        log.info("Paste action");
        pasteText();
    };

    private final ActionListener findListener = e -> {
        log.info("Find action");
    };

    private final ActionListener compileListener = e -> {
        log.info("Compile action");
        compile();
    };

    private final ActionListener compileTapListener = e -> {
        log.info("Compile tap action");
        compileTap();
    };

    private final ActionListener compileTzxListener = e -> {
        log.info("Compile tzx action");
        compileTzx();
    };

    private final ActionListener compileWavListener = e -> {
        log.info("Compile wav action");
        compileWav();
    };

    private final ActionListener cleanConsoleListener = e -> {
        log.info("Clean console");
        cleanConsole();
    };

    private final ChangeListener tabbedPaneChangedListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof JTabbedPane) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                if (index != -1) {
                    final Source oldSrc = mainWindow.getSourceTabbedPane().getSource(index);
                    oldSrc.getTextArea().removeCaretListener(caretListener);
                }
                index = pane.getSelectedIndex();
                if (index != -1) {
                    final Source newSrc = mainWindow.getSourceTabbedPane().getSource(index);
                    newSrc.getTextArea().addCaretListener(caretListener);
                    setPosition(newSrc.getTextArea());
                } else {
                    mainWindow.getStatusPanel().setPosition(0, 0);
                }
            }
        }
    };

    private final CaretListener caretListener = e -> {
        JTextArea textArea = (JTextArea) e.getSource();
        setPosition(textArea);
    };
}
