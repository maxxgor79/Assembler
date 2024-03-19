package ru.retro.assembler.editor.core.ui.find;

import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import ru.retro.assembler.editor.core.util.ResourceUtils;

/**
 * @Author: Maxim Gorin
 * Date: 29.02.2024
 */
@Slf4j
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
        Image image = null;
        try {
            image = ResourceUtils.loadImage("/icon16x16/find.png");
            setIconImage(image);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setTitle(Messages.getInstance().get(Messages.FIND));
        setModal(true);
        setResizable(false);
        setLayout(new BorderLayout());
        textFieldPanel = new TextFieldPanel();
        add(textFieldPanel, BorderLayout.CENTER);
        buttonPanel = new ButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void initListeners() {
        buttonPanel.getBtnCancel().addActionListener(e -> {
            dispose();
        });

        buttonPanel.getBtnFind().addActionListener(e -> {
            result = OPTION_FIND;
            if (textFieldPanel.getTfText().getText().trim().isEmpty()) {
                return;
            }
            dispose();
        });
    }

    public int showModal() {
        getRootPane().setDefaultButton(buttonPanel.getBtnCancel());
        buttonPanel.getBtnCancel().requestFocus();
        result = OPTION_CANCEL;
        setVisible(true);
        return result;
    }
}
