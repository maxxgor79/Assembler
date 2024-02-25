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
            result = OPTION_CANCEL;
            dispose();
        });
        preferencesTabbedPane.getPreferencesPanel().getBtnCompilerPath().addActionListener(l -> {
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getPreferencesPanel().getCompilerPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });

        preferencesTabbedPane.getPreferencesPanel().getBtnOutputDirectory().addActionListener(l -> {
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getPreferencesPanel().getOutputPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });
    }

    public int showModal() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
}
