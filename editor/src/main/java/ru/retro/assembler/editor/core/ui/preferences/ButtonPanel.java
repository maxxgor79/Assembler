package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class ButtonPanel extends JPanel {
    @Getter
    private JButton btnOk;

    @Getter
    private JButton btnCancel;

    public ButtonPanel() {
        initComponents();
        add(btnOk);
        add(btnCancel);
    }

    private void initComponents() {
        btnOk = new JButton(Messages.get(Messages.OK));
        btnOk.setMnemonic('O');
        btnCancel = new JButton(Messages.get(Messages.CANCEL));
        btnCancel.setMnemonic('C');
    }
}
