package ru.retro.assembler.editor.core.ui.replace;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 12.03.2024
 */
@Slf4j
public class ReplaceDialog extends JDialog {
    public static final int OPTION_REPLACE = 1;

    public static final int OPTION_CANCEL = 0;

    private int result;

    @Getter
    private TextFieldsPanel textFieldsPanel;

    @Getter
    private ButtonPanel buttonPanel;

    public ReplaceDialog(Frame owner) {
        super(owner);
        initComponents();
        initListeners();
    }

    private void initComponents() {
        setTitle(Messages.getInstance().get(Messages.REPLACE));
        setModal(true);
        setResizable(false);
        try {
            setIconImage(ResourceUtils.loadImage("/icon16x16/replace.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setLayout(new BorderLayout());
        textFieldsPanel = new TextFieldsPanel();
        add(textFieldsPanel, BorderLayout.CENTER);
        buttonPanel = new ButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void initListeners() {
        buttonPanel.getBtnCancel().addActionListener(e -> {
            dispose();
        });

        buttonPanel.getBtnOK().addActionListener(e -> {
            if (getTextFieldsPanel().getTfOldText().getText().trim().isEmpty() ||
                    getTextFieldsPanel().getTfNewText().getText().trim().isEmpty()) {
                return;
            }
            if (getTextFieldsPanel().getTfOldText().getText().equals(getTextFieldsPanel().getTfNewText().getText())) {
                return;
            }
            result = OPTION_REPLACE;
            dispose();
        });
    }

    public int showModal() {
        result = OPTION_CANCEL;
        getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
        buttonPanel.getBtnCancel().requestFocus();
        setVisible(true);
        return result;
    }
}
