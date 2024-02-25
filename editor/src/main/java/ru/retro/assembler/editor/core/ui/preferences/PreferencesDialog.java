package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.DefaultOpenChooser;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class PreferencesDialog extends JDialog {
    public static int OPTION_SAVE = 1;

    public static int OPTION_CANCEL = 0;

    private int result;

    @Getter
    private ButtonPanel buttonPanel;

    @Getter
    private PreferencesTabbedPane preferencesTabbedPane;

    private DefaultOpenChooser openFileChooser;

    private String[] storedFields = new String[2];

    public PreferencesDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        setTitle(Messages.get(Messages.PREFERENCES));
        setModal(true);
        setLayout(new BorderLayout());
        buttonPanel = new ButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        preferencesTabbedPane = new PreferencesTabbedPane();
        add(preferencesTabbedPane, BorderLayout.CENTER);
        openFileChooser = new DefaultOpenChooser();
        openFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        openFileChooser.setDialogTitle(Messages.get(Messages.CHOOSE_DIRECTORY));
        getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
        buttonPanel.getBtnCancel().requestFocus();
        setResizable(false);
        pack();
        initListeners();
    }

    private void initListeners() {
        buttonPanel.getBtnSave().addActionListener(l -> {
            result = OPTION_SAVE;
            dispose();
        });
        buttonPanel.getBtnCancel().addActionListener(l -> {
            restore();
            dispose();
        });
        preferencesTabbedPane.getPreferencesPanel().getBtnCompilerPath().addActionListener(l -> {
            store();
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getPreferencesPanel().getCompilerPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });
        preferencesTabbedPane.getPreferencesPanel().getBtnOutputDirectory().addActionListener(l -> {
            store();
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getPreferencesPanel().getOutputPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });
    }

    private void store() {
        storedFields[0] = preferencesTabbedPane.getPreferencesPanel().getCompilerPathField().getText();
        storedFields[1] = preferencesTabbedPane.getPreferencesPanel().getOutputPathField().getText();
    }

    private void restore() {
        if (storedFields[0] != null) {
            preferencesTabbedPane.getPreferencesPanel().getCompilerPathField().setText(storedFields[0]);
        }
        if (storedFields[1] != null) {
            preferencesTabbedPane.getPreferencesPanel().getOutputPathField().setText(storedFields[1]);
        }
    }

    public int showModal() {
        result = OPTION_CANCEL;
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
}
