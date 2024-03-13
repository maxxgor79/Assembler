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
    private JButton btnReplace;

    @Getter
    private JButton btnCancel;

    public ButtonPanel() {
        initComponents();
    }

    private void initComponents() {
        btnReplace = new JButton(Messages.get(Messages.REPLACE));
        add(btnReplace);
        btnCancel = new JButton(Messages.get(Messages.CANCEL));
        add(btnCancel);
    }
}
