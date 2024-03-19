package ru.retro.assembler.editor.core.ui.replace;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 12.03.2024
 */
public class TextFieldsPanel extends JPanel {
    @Getter
    private JTextField tfOldText;

    @Getter
    private JTextField tfNewText;

    @Getter
    private JCheckBox cbAll;

    public TextFieldsPanel() {
        initComponents();
    }

    private void initComponents() {
        tfOldText = new JTextField(32);
        tfNewText = new JTextField(32);
        cbAll = new JCheckBox(Messages.getInstance().get(Messages.ALL));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(2, 4, 2, 4);
        add(tfOldText, c);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(2, 4, 2, 4);
        add(tfNewText, c);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(2, 4, 2, 4);
        c.anchor = GridBagConstraints.WEST;
        add(cbAll, c);
    }
}
