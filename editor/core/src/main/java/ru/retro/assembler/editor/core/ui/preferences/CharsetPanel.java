package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 02.03.2024
 */
public class CharsetPanel extends JPanel {
    private static final String[] ITEMS = {
            "ASCII",
            "Windows-1251",
            "ISO-8859-1",
            "UTF-8",
            "UTF-16"
    };

    @Getter
    private JComboBox<String> cbEncoding;

    public CharsetPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        final JLabel label1 = new JLabel(Messages.getInstance().get(Messages.ENCODING));
        add(label1);
        cbEncoding = new JComboBox<>(ITEMS);
        add(cbEncoding);
    }

}
