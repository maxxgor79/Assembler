package ru.retro.assembler.editor.core.ui.address;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 05.04.2024
 */
public class ButtonPanel extends JPanel {
    @Getter
    private JButton btnOk;

    public ButtonPanel() {
        initComponents();
    }

    private void initComponents() {
        btnOk = new JButton(Messages.getInstance().get(Messages.OK));
        btnOk.setMnemonic('o');
        add(btnOk);
    }
}
