package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2024
 */
public class OtherPanel extends JPanel {
    private static final String[] ITEMS = {
            "ASCII",
            "Windows-1251",
            "ISO-8859-1",
            "UTF-8",
            "UTF-16"
    };
    @Getter
    private JComboBox<String> cbEncoding;

    public OtherPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        JLabel label1 = new JLabel(Messages.get(Messages.ENCODING));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 4, 0, 4);
        add(label1, c);
        cbEncoding = new JComboBox<>(ITEMS);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cbEncoding, c);
    }
}
