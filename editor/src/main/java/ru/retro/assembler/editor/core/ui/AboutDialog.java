package ru.retro.assembler.editor.core.ui;

import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 24.02.2024
 */
public class AboutDialog extends JDialog {
    private JButton btnClose;

    public AboutDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        setModal(true);
        setLayout(new BorderLayout());
        JPanel btnPanel = new JPanel();
        btnClose = new JButton(Messages.get(Messages.CLOSE));
        btnClose.addActionListener(l -> AboutDialog.this.setVisible(false));
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
