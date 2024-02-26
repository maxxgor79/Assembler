package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.DefaultOpenChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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

    private final String[] storedFields = new String[3];

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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                store();
            }
        });
        buttonPanel.getBtnSave().addActionListener(l -> {
            result = OPTION_SAVE;
            dispose();
        });
        buttonPanel.getBtnCancel().addActionListener(l -> {
            dispose();
        });
        preferencesTabbedPane.getCompilerPanel().getBtnCompilerPath().addActionListener(l -> {
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getCompilerPanel().getCompilerPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });
        preferencesTabbedPane.getCompilerPanel().getBtnOutputDirectory().addActionListener(l -> {
            if (openFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                preferencesTabbedPane.getCompilerPanel().getOutputPathField().setText(openFileChooser
                        .getSelectedFile().getAbsolutePath());
            }
        });
    }

    private void store() {
        storedFields[0] = preferencesTabbedPane.getCompilerPanel().getCompilerPathField().getText();
        storedFields[1] = preferencesTabbedPane.getCompilerPanel().getOutputPathField().getText();
        storedFields[2] = (String) preferencesTabbedPane.getOtherPanel().getCbEncoding().getSelectedItem();
    }

    private void restore() {
        if (storedFields[0] != null) {
            preferencesTabbedPane.getCompilerPanel().getCompilerPathField().setText(storedFields[0]);
        }
        if (storedFields[1] != null) {
            preferencesTabbedPane.getCompilerPanel().getOutputPathField().setText(storedFields[1]);
        }
        if (storedFields[2] != null) {
            preferencesTabbedPane.getOtherPanel().getCbEncoding().setSelectedItem(storedFields[2]);
        }
    }

    public int showModal() {
        result = OPTION_CANCEL;
        setLocationRelativeTo(getOwner());
        setVisible(true);
        if (result == OPTION_CANCEL) {
            restore();
        }
        return result;
    }
}
