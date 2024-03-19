package ru.retro.assembler.editor.core.ui.replace;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 12.03.2024
 */
public class ButtonPanel  extends JPanel {
    @Getter
    private JButton btnOK;

    @Getter
    private JButton btnCancel;

    public ButtonPanel() {
        initComponents();
    }

    private void initComponents() {
        btnOK = new JButton(Messages.getInstance().get(Messages.OK));
        btnOK.setMnemonic('O');
        add(btnOK);
        btnCancel = new JButton(Messages.getInstance().get(Messages.CANCEL));
        btnOK.setMnemonic('C');
        add(btnCancel);
    }
}
