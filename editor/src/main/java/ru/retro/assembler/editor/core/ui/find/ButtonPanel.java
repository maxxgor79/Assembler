package ru.retro.assembler.editor.core.ui.find;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 29.02.2024
 */
public class ButtonPanel extends JPanel {
    @Getter
    private JButton btnFind;

    @Getter
    private JButton btnCancel;

    public ButtonPanel() {
        initComponents();
    }

    private void initComponents() {
        btnFind = new JButton(Messages.get(Messages.FIND));
        add(btnFind);
        btnCancel = new JButton(Messages.get(Messages.CANCEL));
        add(btnCancel);
    }
}
