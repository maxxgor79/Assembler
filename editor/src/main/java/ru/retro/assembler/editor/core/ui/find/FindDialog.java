package ru.retro.assembler.editor.core.ui.find;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 29.02.2024
 */
public class FindDialog extends JDialog {
    public static final int OPTION_FIND = 1;

    public static final int OPTION_CANCEL = 0;

    private int result;

    @Getter
    private TextFieldPanel textFieldPanel;

    @Getter
    private ButtonPanel buttonPanel;

    public FindDialog(Frame owner) {
        super(owner);
        initComponents();
        initListeners();
    }

    private void initComponents() {
        setTitle(Messages.get(Messages.FIND));
        setModal(true);
        setResizable(false);
        setLayout(new BorderLayout());
        textFieldPanel = new TextFieldPanel();
        add(textFieldPanel, BorderLayout.CENTER);
        buttonPanel = new ButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
        buttonPanel.getBtnCancel().requestFocus();
        pack();
    }

    private void initListeners() {
        buttonPanel.getBtnCancel().addActionListener(e -> {
            dispose();
        });

        buttonPanel.getBtnFind().addActionListener(e -> {
            result = OPTION_FIND;
            dispose();
        });
    }

    public int showModal() {
        result = OPTION_CANCEL;
        setVisible(true);
        return result;
    }
}
