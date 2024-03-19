package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import ru.retro.assembler.editor.core.env.Environment;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.ui.find.FindDialog;
import ru.retro.assembler.editor.core.ui.menu.build.EmptyMenuItems;
import ru.retro.assembler.editor.core.ui.menu.build.EmptyToolButtons;
import ru.retro.assembler.editor.core.ui.preferences.ColorPanel;
import ru.retro.assembler.editor.core.ui.preferences.PreferencesDialog;
import ru.retro.assembler.editor.core.ui.replace.ReplaceDialog;
import ru.retro.assembler.editor.core.util.*;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Locale;
import java.util.prefs.BackingStoreException;

/**
 * Author: Maxim Gorin Date: 26.02.2024
 */
@Slf4j
public final class Controller implements Runnable {

    protected static final int TIMER_DELAY = 250;

    protected static final String NEW_SOURCE_NAME = "noname.asm";

    @Getter
    protected final AppSettings settings = new AppSettings();

    @Getter
    protected MainWindow mainWindow;

    private ModalDialog aboutDialog;

    private PreferencesDialog preferencesDialog;

    private FindDialog findDialog;

    private ReplaceDialog replaceDialog;

    private URI helpUri;

    private Timer timer;

    private int index = -1;

    private JFileChooser saveAsFileChooser;

    private JFileChooser openFileChooser;

    @Getter
    @Setter
    @NonNull
    private static FileChooserFactory fileChooserFactory = EmptyFileChoosers.defaultFileChooserFactory();

    @Getter
    @Setter
    @NonNull
    private static MenuItemFactory menuItemFactory = EmptyMenuItems.defaultMenuItemFactory();

    @Getter
    @Setter
    @NonNull
    private static ToolButtonFactory toolButtonFactory = EmptyToolButtons.defaultToolButtonFactory();

    @Getter
    @Setter
    @NonNull
    private static UIFactory uiFactory = EmptyUIComponents.defaultUIFactory();

    @Getter
    private Collection<String> args;

    public Controller(final Collection<String> args) {
        loadSettings();
        this.args = args;
        this.mainWindow = new MainWindow();
        this.mainWindow.setLocationByPlatform(true);
        this.preferencesDialog = new PreferencesDialog(mainWindow);
        this.findDialog = new FindDialog(mainWindow);
        this.replaceDialog = new ReplaceDialog(mainWindow);
        initListeners();
    }

    private ModalDialog getAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = uiFactory.newAboutDialog(this);
        }
        return aboutDialog;
    }

    private JFileChooser getSaveAsFileChooser() {
        if (saveAsFileChooser == null) {
            saveAsFileChooser = fileChooserFactory.newSaveChooser();
            if (settings.getSaveDialogCurrentDirectory() != null) {
                saveAsFileChooser.setCurrentDirectory(new File(settings.getSaveDialogCurrentDirectory()));
            }
        }
        return saveAsFileChooser;
    }

    private JFileChooser getOpenFileChooser() {
        if (openFileChooser == null) {
            openFileChooser = fileChooserFactory.newOpenChooser();
            if (settings.getOpenDialogCurrentDirectory() != null) {
                openFileChooser.setCurrentDirectory(new File(settings.getOpenDialogCurrentDirectory()));
            }
        }
        return openFileChooser;
    }

    @Override
    public void run() {
        Environment.getInstance().setMainWindow(mainWindow);
        apply(settings);
        mainWindow.setVisible(true);
        if (args != null && !args.isEmpty()) {
            for (String fileName : args) {
                openSource(new File(fileName));
            }
        }
    }

    private void loadSettings() {
        try {
            settings.load();
            log.info("Setting are loaded successfully");
        } catch (BackingStoreException e) {
            log.error(e.getMessage(), e);
        }
        if (settings.getLanguage() != null) {
            setLocale(UIUtils.toLocale(settings.getLanguage()));
        }
        log.info("Build successfully");
    }

    private void saveSettings() {
        try {
            store(settings);
            settings.save();
            log.info("Setting are saved successfully");
        } catch (BackingStoreException e) {
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
        if (settings.getEncoding() != null) {
            preferencesDialog.getPreferencesTabbedPane().getMiscellaneousPanel().getCharsetPanel().getCbEncoding()
                    .setSelectedItem(settings.getEncoding().toUpperCase());
        }

        if (settings.getConsoleFontName() != null) {
            preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                    .getFontPanel().setSelectedFontName(settings.getConsoleFontName());
        }
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                .getFontSizePanel().setValue(settings.getConsoleFontSize());
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                .getBkColorPanel()
                .setColor(new Color(settings.getConsoleBkColor()));
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                .getFontColorPanel()
                .setColor(new Color(settings.getConsoleFontColor()));
        if (settings.getEditorFontName() != null) {
            preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getEditorAppearancePanel()
                    .getFontPanel()
                    .setSelectedFontName(settings.getEditorFontName());
        }
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getEditorAppearancePanel()
                .getFontSizePanel()
                .setValue(settings.getEditorFontSize());
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getEditorAppearancePanel()
                .getBkColorPanel()
                .setColor(new Color(settings.getEditorBkColor()));
        if (settings.getLanguage() != null) {
            preferencesDialog.getPreferencesTabbedPane().getMiscellaneousPanel().getLanguagePanel()
                    .getCbLanguages()
                    .setSelectedItem(settings.getLanguage());
        } else {
            preferencesDialog.getPreferencesTabbedPane().getMiscellaneousPanel().getLanguagePanel()
                    .getCbLanguages()
                    .setSelectedItem(UIUtils.toLanguage(Messages.getLocale()));
        }
        preferencesDialog.getPreferencesTabbedPane().getCompilerPanel().getCbEmbedded()
                .setSelected(settings.isCompiledEmbedded());
        if (settings.getEditorFontName() != null) {
            Environment.getInstance().setEditorFont(UIUtils.createFont(settings.getEditorFontName()
                    , settings.getEditorFontSize()));
        }
        Environment.getInstance().setEditorBkColor(new Color(settings.getEditorBkColor()));
    }

    protected void store(@NonNull final AppSettings settings) {
        mainWindow.store(settings);
    }

    //------------------------------------------------------------------------------------------------------------------
    private void createBuildToolButtons() {
        final Collection<ToolButton> buildToolButtons = toolButtonFactory.newToolButtons(this);
        if (buildToolButtons != null) {
            for (ToolButton button : buildToolButtons) {
                mainWindow.getToolBarButtons().add(button);
            }
        }
    }

    private void createMenuItems() {
        final Collection<MenuItem> buildMenuItems = menuItemFactory.newMenuItems(this);
        if (buildMenuItems != null) {
            for (MenuItem menuItem : buildMenuItems) {
                mainWindow.getBuildMenuItems().add(menuItem);
            }
        }
    }

    private void createIcons() {
        mainWindow.setTaskBar(uiFactory.newTaskBarImage());
        mainWindow.setIconImage(uiFactory.newWindowImage());
        preferencesDialog.setIconImage(uiFactory.newWindowImage());
    }

    protected void initListeners() {
        createIcons();
        createBuildToolButtons();
        createMenuItems();
        mainWindow.addWindowListener(windowAdapter);
        mainWindow.getHelpMenuItems().getMiHelp().addActionListener(helpListener);
        mainWindow.getHelpMenuItems().getMiAbout().addActionListener(aboutListener);
        mainWindow.getToolsMenuItems().getMiPreferences().addActionListener(preferencesListener);
        mainWindow.getBtnNew().addActionListener(newListener);
        mainWindow.getBtnOpen().addActionListener(openListener);
        mainWindow.getBtnSave().addActionListener(saveListener);
        mainWindow.getBtnReload().addActionListener(reloadAllListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getFileMenuItems().getMiNew().addActionListener(newListener);
        mainWindow.getFileMenuItems().getMiOpen().addActionListener(openListener);
        mainWindow.getFileMenuItems().getMiSave().addActionListener(saveListener);
        mainWindow.getFileMenuItems().getMiSaveAs().addActionListener(saveAsListener);
        mainWindow.getFileMenuItems().getMiSaveAll().addActionListener(saveAllListener);
        mainWindow.getFileMenuItems().getMiClose().addActionListener(closeListener);
        mainWindow.getFileMenuItems().getMiCloseAll().addActionListener(closeAllListener);
        mainWindow.getFileMenuItems().getMiReloadAllFromDisk().addActionListener(reloadAllListener);
        mainWindow.getFileMenuItems().getMiPrint().addActionListener(printListener);
        mainWindow.getFileMenuItems().getMiExit().addActionListener(exitListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getEditMenuItems().getMiUndo().addActionListener(undoListener);
        mainWindow.getEditMenuItems().getMiCopy().addActionListener(copyListener);
        mainWindow.getEditMenuItems().getMiCut().addActionListener(cutListener);
        mainWindow.getEditMenuItems().getMiPaste().addActionListener(pasteListener);
        mainWindow.getEditMenuItems().getMiDelete().addActionListener(deleteListener);
        mainWindow.getEditMenuItems().getMiSelectAll().addActionListener(selectAllListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getEditMenuItems().getMiFind().addActionListener(findListener);
        mainWindow.getEditMenuItems().getMiFindNext().addActionListener(findNextListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getEditMenuItems().getMiReplace().addActionListener(replaceListener);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiClose()
                .addActionListener(closeListener);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSave()
                .addActionListener(saveListener);
        mainWindow.getSourceTabbedPane().getSourcePopupMenu().getMiSaveAs()
                .addActionListener(saveAsListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getSourceTabbedPane().addChangeListener(tabbedPaneChangedListener);
        //--------------------------------------------------------------------------------------------------------------
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getEditorAppearancePanel()
                .getBkColorPanel()
                .addActionListener(chooseColorActionListener);
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                .getFontColorPanel()
                .addActionListener(chooseColorActionListener);
        preferencesDialog.getPreferencesTabbedPane().getAppearancePanel().getConsoleAppearancePanel()
                .getBkColorPanel()
                .addActionListener(chooseColorActionListener);
        //--------------------------------------------------------------------------------------------------------------
        mainWindow.getConsole().getConsolePopupMenu().getMiClean()
                .addActionListener(cleanConsoleListener);
        timer = new Timer(TIMER_DELAY, new Activator(mainWindow));
        timer.start();
    }

    //------------------------------------------------------------------------------------------------------------------
    protected void setLocale(@NonNull Locale locale) {
        Messages.setLocale(locale);
        UIUtils.updateUIManager();
    }

    protected static Source createSource(@NonNull File file) {
        Source src = new Source(file);
        if (Environment.getInstance().getEditorFont() != null) {
            src.getTextArea().setFont(Environment.getInstance().getEditorFont());
        }
        src.getTextArea().setBackground(Environment.getInstance().getEditorBkColor());
        return src;
    }

    private void newSource() {
        Source newSrc = createSource(new File(NEW_SOURCE_NAME));
        mainWindow.getSourceTabbedPane().add(newSrc);
    }

    private void openSource(@NonNull File file) {
        try {
            final Source src = createSource(file);
            src.load(file, settings.getEncoding());
            int tabIndex = mainWindow.getSourceTabbedPane().indexOf(src);
            if (tabIndex == -1) {
                mainWindow.getSourceTabbedPane().add(src);
            } else {
                final Source existedSource = mainWindow.getSourceTabbedPane().getSource(tabIndex);
                existedSource.setContent(src.getContent());
                mainWindow.getSourceTabbedPane().setSelected(existedSource);
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow, Messages.getInstance().get(Messages.ENCODING_ERROR),
                    Messages.getInstance().get(Messages
                            .ERROR), JOptionPane.ERROR_MESSAGE, ResourceUtils.getErrorIcon());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(mainWindow,
                    String.format(Messages.getInstance().get(Messages.IO_ERROR), file.getAbsolutePath())
                    , Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE, ResourceUtils.getErrorIcon());
        }
    }

    private void openSource() {
        final JFileChooser openFileChooser = getOpenFileChooser();
        if (openFileChooser == null) {
            log.info("openFileChooser is null");
        }
        if (openFileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            settings.setOpenDialogCurrentDirectory(
                    openFileChooser.getCurrentDirectory().getAbsolutePath());
            for (File file : openFileChooser.getSelectedFiles()) {
                openSource(file);
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
            JOptionPane.showMessageDialog(mainWindow, String.format(Messages.getInstance().get(Messages.IO_ERROR)
                            , file.getAbsolutePath()), Messages.getInstance().get(Messages.ERROR), JOptionPane.ERROR_MESSAGE
                    , ResourceUtils.getErrorIcon());
        }
    }

    private void overwriteSave(@NonNull File file, @NonNull Source src) throws IOException {
        if (file.exists()) {
            int option = JOptionPane.showConfirmDialog(mainWindow, Messages.getInstance().get(Messages.OVERWRITE_FILE)
                    , Messages.getInstance().get(Messages.SAVE), JOptionPane.YES_NO_OPTION, JOptionPane
                            .QUESTION_MESSAGE, ResourceUtils.getQuestionIcon());
            if (option == JOptionPane.NO_OPTION) {
                return;
            }
        }
        src.save(file, settings.getEncoding());
    }

    private void saveSourceAs(@NonNull Source src) {
        final JFileChooser saveAsChooser = getSaveAsFileChooser();
        if (saveAsChooser == null) {
            log.info("saveAsChooser is null");
            return;
        }
        saveAsChooser.setSelectedFile(src.getFile());
        if (saveAsChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            final File file = saveAsChooser.getSelectedFile();
            settings.setSaveDialogCurrentDirectory(
                    saveAsChooser.getCurrentDirectory().getAbsolutePath());
            try {
                overwriteSave(file, src);
                src.rename(file);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, String.format(Messages.getInstance().get(Messages.IO_ERROR)
                                , file.getAbsolutePath()), Messages.getInstance().get(Messages.ERROR), JOptionPane
                                .ERROR_MESSAGE, ResourceUtils.getErrorIcon());
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
            final Source src = mainWindow.getSourceTabbedPane().getSource(i);
            if (src.isNew()) {
                continue;
            }
            try {
                if (src.hasChanges()) {
                    final String message = String.format(Messages.getInstance().get(Messages.SOURCE_CONTAINS_CHANGES),
                            src.getFile()
                                    .getName());
                    final int option = JOptionPane.showConfirmDialog(mainWindow, message,
                            Messages.getInstance().get(Messages.SAVE)
                            , JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                            , ResourceUtils.getQuestionIcon());
                    if (option == JOptionPane.YES_OPTION) {
                        src.save(settings.getEncoding());
                        continue;
                    }
                }
                src.load();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(mainWindow, String.format(Messages.getInstance().get(Messages.IO_ERROR)
                        , src.getFile().getAbsolutePath()), Messages.getInstance().get(Messages.ERROR), JOptionPane
                        .ERROR_MESSAGE, ResourceUtils.getErrorIcon());
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
            final String message = String.format(Messages.getInstance().get(Messages.SOURCE_IS_UNSAVED), src.getName());
            final int option = JOptionPane.showConfirmDialog(mainWindow, message,
                    Messages.getInstance().get(Messages.SAVE), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    ResourceUtils.getQuestionIcon());
            if (option == JOptionPane.YES_OPTION) {
                saveSourceAs(src);
            }
        } else if (src.hasChanges()) {
            final String message = String.format(Messages.getInstance().get(Messages.SOURCE_CONTAINS_CHANGES),
                    src.getName());
            final int option = JOptionPane.showConfirmDialog(mainWindow, message,
                    Messages.getInstance().get(Messages.SAVE), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    ResourceUtils.getQuestionIcon());
            if (option == JOptionPane.YES_OPTION) {
                try {
                    src.save(settings.getEncoding());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(mainWindow,
                            String.format(Messages.getInstance().get(Messages.IO_ERROR), src
                                    .getFile().getAbsolutePath()), Messages.getInstance().get(Messages.ERROR), JOptionPane
                                    .ERROR_MESSAGE, ResourceUtils.getErrorIcon());
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
        int option = JOptionPane.showConfirmDialog(mainWindow, Messages.getInstance().get(Messages.CLOSE_APPLICATION)
                , Messages.getInstance().get(Messages.CLOSE), JOptionPane.YES_NO_OPTION
                , JOptionPane.QUESTION_MESSAGE, ResourceUtils.getQuestionIcon());
        if (option == JOptionPane.YES_OPTION) {
            closeAll();
            mainWindow.dispose();
            timer.stop();
            saveSettings();
        }
    }

    public void cleanConsole() {
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

    private void deleteText() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().replaceSelection("");
    }

    private void selectAllText() {
        final Source selectedSource = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (selectedSource == null) {
            return;
        }
        selectedSource.getTextArea().selectAll();
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

    private void setCaption(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            mainWindow.setDefaultTitle();
        } else {
            mainWindow.setTitle(String.format("%s - %s", Messages.getInstance().get(Messages.CAPTION), fileName));
        }
    }

    private void find() {
        final Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (src == null) {
            return;
        }
        findDialog.setLocationRelativeTo(mainWindow);
        int option = findDialog.showModal();
        if (option == FindDialog.OPTION_FIND) {
            final RSyntaxTextArea textArea = src.getTextArea();
            int startIndex = textArea.getLineStartOffsetOfCurrentLine() + textArea.getCaretPosition();
            final String text = textArea.getText();
            final String occurrence = findDialog.getTextFieldPanel().getTfText().getText().trim();
            final int index = text.indexOf(occurrence, startIndex);
            if (index != -1) {
                textArea.requestFocus();
                final int lastIndex = index + occurrence.length();
                textArea.select(index, lastIndex);
                Environment.getInstance().setNextOccurrenceIndex(lastIndex);
                Environment.getInstance().setOccurrence(occurrence);
            } else {
                Environment.getInstance().setNextOccurrenceIndex(-1);
                Environment.getInstance().setOccurrence(null);
                JOptionPane.showMessageDialog(mainWindow,
                        String.format(Messages.getInstance().get(Messages.OCCURRENCE_NOT_FOUND), occurrence), Messages
                                .getInstance().get(Messages.OCCURRENCE), JOptionPane.INFORMATION_MESSAGE
                        , ResourceUtils.getInformationIcon());
            }
        }
    }

    private void findNext() {
        final String occurrence = Environment.getInstance().getOccurrence();
        final int occurrenceIndex = Environment.getInstance().getNextOccurrenceIndex();
        if (occurrenceIndex == -1 || occurrence == null) {
            return;
        }
        final Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (src == null) {
            return;
        }
        final RSyntaxTextArea textArea = src.getTextArea();
        final String text = src.getTextArea().getText();
        final int index = text.indexOf(occurrence, occurrenceIndex);
        if (index != -1) {
            textArea.requestFocus();
            final int lastIndex = index + occurrence.length();
            textArea.select(index, lastIndex);
            Environment.getInstance().setNextOccurrenceIndex(lastIndex);
        } else {
            Environment.getInstance().setNextOccurrenceIndex(-1);
            Environment.getInstance().setOccurrence(null);
            JOptionPane.showMessageDialog(mainWindow,
                    String.format(Messages.getInstance().get(Messages.OCCURRENCE_NOT_FOUND), occurrence)
                    , Messages.getInstance().get(Messages.OCCURRENCE), JOptionPane.INFORMATION_MESSAGE
                    , ResourceUtils.getInformationIcon());
        }
    }

    private void replace() {
        final Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (src == null) {
            return;
        }
        replaceDialog.setLocationRelativeTo(mainWindow);
        int option = replaceDialog.showModal();
        if (option == ReplaceDialog.OPTION_REPLACE) {
            final TextUtils.ReplaceResult result = TextUtils.replace(src.getTextArea().getText(), replaceDialog
                    .getTextFieldsPanel().getTfOldText().getText(), replaceDialog.getTextFieldsPanel().getTfNewText()
                    .getText(), replaceDialog.getTextFieldsPanel().getCbAll().isSelected());
            if (result.getNumReplaces() > 0) {
                src.getTextArea().setText(result.getText());
            }
            JOptionPane.showMessageDialog(mainWindow,
                    String.format(Messages.getInstance().get(Messages.REPLACES_IS_OCCURRED), result.getNumReplaces())
                    , Messages.getInstance().get(Messages.REPLACE), JOptionPane.INFORMATION_MESSAGE
                    , ResourceUtils.getInformationIcon());
        }
    }

    private void print() {
        final Source src = mainWindow.getSourceTabbedPane().getSourceSelected();
        if (src == null) {
            return;
        }
        final PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(src.getTextArea().getPrintable(null, null));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                log.info(e.getMessage(), e);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private final ActionListener preferencesListener = e -> {
        log.info("Preferences");
        preferencesDialog.setLocationRelativeTo(mainWindow);
        if (preferencesDialog.showModal() == PreferencesDialog.OPTION_OK) {
            final String encoding = (String) preferencesDialog.getPreferencesTabbedPane().getMiscellaneousPanel()
                    .getCharsetPanel().getCbEncoding().getSelectedItem();
            mainWindow.getStatusPanel().setEncoding(encoding);
            settings.setEncoding(encoding);
            final String compilerPath = preferencesDialog.getPreferencesTabbedPane().getCompilerPanel()
                    .getCompilerPathField().getText();
            settings.setCompilerPath(compilerPath);
            final String outputDir = preferencesDialog.getPreferencesTabbedPane().getCompilerPanel()
                    .getOutputPathField().getText();
            settings.setOutputDirectory(outputDir);
            settings.setCompiledEmbedded(preferencesDialog.getPreferencesTabbedPane().getCompilerPanel()
                    .getCbEmbedded().isSelected());
            settings.setConsoleFontName(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getConsoleAppearancePanel().getFontPanel().getSelectedFontName());
            settings.setConsoleFontSize(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getConsoleAppearancePanel().getFontSizePanel().getValue());
            settings.setConsoleBkColor(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getConsoleAppearancePanel().getBkColorPanel().getColor().getRGB());
            settings.setConsoleFontColor(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getConsoleAppearancePanel().getFontColorPanel().getColor().getRGB());
            settings.setEditorFontName(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getEditorAppearancePanel().getFontPanel().getSelectedFontName());
            settings.setEditorFontSize(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getEditorAppearancePanel().getFontSizePanel().getValue());
            settings.setEditorBkColor(preferencesDialog.getPreferencesTabbedPane().getAppearancePanel()
                    .getEditorAppearancePanel().getBkColorPanel().getColor().getRGB());
            settings.setLanguage(
                    preferencesDialog.getPreferencesTabbedPane().getMiscellaneousPanel().getLanguagePanel()
                            .getCbLanguages().getSelectedItem().toString());
            Environment.getInstance().setEditorFont(UIUtils.createFont(settings.getEditorFontName()
                    , settings.getEditorFontSize()));
            Environment.getInstance().setEditorBkColor(new Color(settings.getEditorBkColor()));
            mainWindow.applyFontAndColor(settings);
            if (!UIUtils.equals(UIUtils.toLocale(settings.getLanguage()), Messages.getLocale())) {
                JOptionPane.showMessageDialog(mainWindow, Messages.getInstance().get(Messages.RESTART_TO_CHANGE_LANG),
                        Messages
                                .getInstance().get(Messages.WARNING), JOptionPane.WARNING_MESSAGE);
            }
        }
    };

    private final WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            closeWindow();
        }
    };

    private final ActionListener helpListener = e -> {
        ResourceUtils.browse(helpUri);
    };

    private final ActionListener aboutListener = e -> {
        final ModalDialog aboutDialog = getAboutDialog();
        if (aboutDialog == null) {
            log.info("aboutDialog is null");
            return;
        }
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

    private final ActionListener printListener = e -> {
        log.error("print");
        print();
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

    private final ActionListener deleteListener = e -> {
        log.info("Delete action");
        deleteText();
    };

    private final ActionListener selectAllListener = e -> {
        log.info("Select all action");
        selectAllText();
    };

    private final ActionListener findListener = e -> {
        log.info("Find action");
        find();
    };

    private final ActionListener replaceListener = e -> {
        log.info("Replace action");
        replace();
    };

    private final ActionListener findNextListener = e -> {
        log.info("Find next occurrence");
        findNext();
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
                    oldSrc.getTextArea().removeKeyListener(textAreaKeyListener);
                }
                index = pane.getSelectedIndex();
                if (index != -1) {
                    final Source newSrc = mainWindow.getSourceTabbedPane().getSource(index);
                    newSrc.getTextArea().addCaretListener(caretListener);
                    newSrc.getTextArea().addKeyListener(textAreaKeyListener);
                    setPosition(newSrc.getTextArea());
                    setCaption(newSrc.getFile().getName());
                } else {
                    mainWindow.getStatusPanel().setPosition(0, 0);
                    setCaption(null);
                }
            }
        }
    };

    private final CaretListener caretListener = e -> {
        JTextArea textArea = (JTextArea) e.getSource();
        setPosition(textArea);
    };

    private final KeyListener textAreaKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_L) {
                findNext();
            }
        }
    };

    private final ActionListener chooseColorActionListener = e -> {
        final ColorPanel colorPanel = (ColorPanel) e.getSource();
        final Color c = JColorChooser.showDialog(mainWindow, Messages.getInstance().get(Messages.COLOR_CHOICE),
                colorPanel.getColor());
        if (c != null) {
            colorPanel.setColor(c);
        }
    };
}
