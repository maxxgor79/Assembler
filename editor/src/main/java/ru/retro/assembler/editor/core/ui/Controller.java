package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.preferences.PreferencesDialog;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2024
 */
@Slf4j
public final class Controller implements Runnable {
    protected static final String SETTINGS_FILENAME = "editor.properties";

    protected final AppSettings SETTINGS = new AppSettings();

    protected MainWindow mainWindow;

    private AboutDialog aboutDialog;

    private PreferencesDialog preferencesDialog;

    private URI helpUri;

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
            SETTINGS.load(fis);
            log.info("Setting are loaded successfully");
        } catch (ConfigurationException | IOException e) {
            log.error(e.getMessage(), e);
        }
        apply(SETTINGS);
    }

    private void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILENAME)) {
            store(SETTINGS);
            SETTINGS.save(fos);
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

    //----------------------------------------------------------------------------------------------------------------------
    protected void initListeners() {
        mainWindow.addWindowListener(windowAdapter);
        mainWindow.getHelpMenuItems().getMiHelp().addActionListener(helpListener);
        mainWindow.getHelpMenuItems().getMiAbout().addActionListener(aboutListener);
        mainWindow.getToolsMenuItems().getMiPreferences().addActionListener(preferencesListener);
    }

    private final ActionListener preferencesListener = e -> {
        if (preferencesDialog.showModal() == PreferencesDialog.OPTION_SAVE) {
            mainWindow.getStatusPanel().setEncoding((String) preferencesDialog.getPreferencesTabbedPane()
                    .getOtherPanel().getCbEncoding().getSelectedItem());
        }
    };

    private final WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            saveSettings();
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
}
