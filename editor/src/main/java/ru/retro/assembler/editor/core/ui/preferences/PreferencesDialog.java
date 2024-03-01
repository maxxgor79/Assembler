package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class PreferencesDialog extends JDialog {
    private static final int C_PATH = 0;

    private static final int C_OUTPUT = 1;

    private static final int ENCODING = 2;

    private static final int EDITOR_FONT = 3;

    private static final int EDITOR_FONT_SIZE = 4;

    private static final int EDITOR_BK_COLOR = 5;

    private static final int CONSOLE_FONT = 6;

    private static final int CONSOLE_FONT_SIZE = 7;

    private static final int CONSOLE_BK_COLOR = 8;

    private static final int CONSOLE_FONT_COLOR = 9;


    public static int OPTION_OK = 1;

    public static int OPTION_CANCEL = 0;

    private int result;

    @Getter
    private ButtonPanel buttonPanel;

    @Getter
    private PreferencesTabbedPane preferencesTabbedPane;

    private LocalizedOpenChooser openFileChooser;

    private final String[] storedFields = new String[10];

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
        openFileChooser = new LocalizedOpenChooser();
        openFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        openFileChooser.setDialogTitle(Messages.get(Messages.CHOOSE_DIRECTORY));
        getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
        buttonPanel.getBtnCancel().requestFocus();
        setResizable(false);
        pack();
        initListeners();
    }

    private void initListeners() {
        buttonPanel.getBtnOk().addActionListener(l -> {
            result = OPTION_OK;
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

    protected void store() {
        storedFields[C_PATH] = preferencesTabbedPane.getCompilerPanel().getCompilerPathField().getText();
        storedFields[C_OUTPUT] = preferencesTabbedPane.getCompilerPanel().getOutputPathField().getText();
        storedFields[ENCODING] = (String) preferencesTabbedPane.getOtherPanel().getCbEncoding().getSelectedItem();
        storedFields[EDITOR_FONT] = preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel()
                .getFontPanel().getSelectedFontName();
        storedFields[EDITOR_FONT_SIZE] = String.valueOf(preferencesTabbedPane.getAppearancePanel()
                .getEditorAppearancePanel().getFontSizePanel().getValue());
        storedFields[EDITOR_BK_COLOR] = String.valueOf(preferencesTabbedPane.getAppearancePanel()
                .getEditorAppearancePanel().getBkColorPanel().getColor().getRGB());
        storedFields[CONSOLE_FONT] = preferencesTabbedPane.getAppearancePanel()
                .getConsoleAppearancePanel().getFontPanel().getSelectedFontName();
        storedFields[CONSOLE_FONT_SIZE] = String.valueOf(preferencesTabbedPane.getAppearancePanel()
                .getConsoleAppearancePanel().getFontSizePanel().getValue());
        storedFields[CONSOLE_BK_COLOR] = String.valueOf(preferencesTabbedPane.getAppearancePanel()
                .getConsoleAppearancePanel().getBkColorPanel().getColor().getRGB());
        storedFields[CONSOLE_FONT_COLOR] = String.valueOf(preferencesTabbedPane.getAppearancePanel()
                .getConsoleAppearancePanel().getFontColorPanel().getColor().getRGB());
    }

    protected void restore() {
        if (storedFields[C_PATH] != null) {
            preferencesTabbedPane.getCompilerPanel().getCompilerPathField().setText(storedFields[C_PATH]);
            storedFields[C_PATH] = null;
        }
        if (storedFields[C_OUTPUT] != null) {
            preferencesTabbedPane.getCompilerPanel().getOutputPathField().setText(storedFields[C_OUTPUT]);
            storedFields[C_OUTPUT] = null;
        }
        if (storedFields[ENCODING] != null) {
            preferencesTabbedPane.getOtherPanel().getCbEncoding().setSelectedItem(storedFields[ENCODING]);
            storedFields[ENCODING] = null;
        }
        if (storedFields[EDITOR_FONT] != null) {
            preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getFontPanel()
                    .setSelectedFontName(storedFields[EDITOR_FONT]);
            storedFields[EDITOR_FONT] = null;
        }
        if (storedFields[EDITOR_FONT_SIZE] != null) {
            preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getFontSizePanel()
                    .setValue(Integer.parseInt(storedFields[EDITOR_FONT_SIZE]));
            storedFields[EDITOR_FONT_SIZE] = null;
        }
        if (storedFields[EDITOR_BK_COLOR] != null) {
            preferencesTabbedPane.getAppearancePanel().getEditorAppearancePanel().getBkColorPanel()
                    .setColor(new Color(Integer.parseInt(storedFields[EDITOR_BK_COLOR])));
            storedFields[EDITOR_BK_COLOR] = null;
        }
        if (storedFields[CONSOLE_FONT] != null) {
            preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontPanel()
                    .setSelectedFontName(storedFields[CONSOLE_FONT]);
            storedFields[CONSOLE_FONT] = null;
        }
        if (storedFields[CONSOLE_FONT_SIZE] != null) {
            preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontSizePanel()
                    .setValue(Integer.parseInt(storedFields[CONSOLE_FONT_SIZE]));
            storedFields[CONSOLE_FONT_SIZE] = null;
        }
        if (storedFields[CONSOLE_BK_COLOR] != null) {
            preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getBkColorPanel()
                    .setColor(new Color(Integer.parseInt(storedFields[CONSOLE_BK_COLOR])));
            storedFields[CONSOLE_BK_COLOR] = null;
        }
        if (storedFields[CONSOLE_FONT_COLOR] != null) {
            preferencesTabbedPane.getAppearancePanel().getConsoleAppearancePanel().getFontColorPanel()
                    .setColor(new Color(Integer.parseInt(storedFields[CONSOLE_FONT_COLOR])));
            storedFields[CONSOLE_FONT_COLOR] = null;
        }
    }

    public int showModal() {
        result = OPTION_CANCEL;
        store();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        if (result == OPTION_CANCEL) {
            restore();
        }
        return result;
    }
}
