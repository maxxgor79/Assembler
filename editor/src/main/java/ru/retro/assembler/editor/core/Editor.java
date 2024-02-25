package ru.retro.assembler.editor.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import ru.retro.assembler.editor.core.settings.AppSettings;
import ru.retro.assembler.editor.core.ui.MainWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public class Editor {
    private static final String SETTINGS_FILENAME = "editor.properties";

    private static final AppSettings SETTINGS = new AppSettings();

    private static MainWindow window;

    public static void main(String[] args) {
        //Locale.setDefault(new Locale("ru"));
        window = new MainWindow();
        if (!loadSettings()) {
            setDefaultSettings();
        }
        initListeners();
        SwingUtilities.invokeLater(() -> {
            window.setVisible(true);
        });
    }

    private static void setDefaultSettings() {
        window.apply(SETTINGS);
        window.setLocationRelativeTo(null);
    }

    private static boolean loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILENAME)) {
            SETTINGS.load(fis);
            window.apply(SETTINGS);
            log.info("Setting are loaded successfully");
            return true;
        } catch (ConfigurationException | IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static void saveSettings() {
        try(FileOutputStream fos = new FileOutputStream(SETTINGS_FILENAME)) {
            SETTINGS.save(fos);
            log.info("Setting are saved successfully");
        }
        catch (ConfigurationException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void initListeners() {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.store(SETTINGS);
                saveSettings();
                e.getWindow().dispose();
            }
        });
    }
}
