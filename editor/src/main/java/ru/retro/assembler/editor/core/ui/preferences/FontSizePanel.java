package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class FontSizePanel extends JPanel {
    @Getter
    private JComboBox<Integer> cbFontSize = new JComboBox<>();

    public FontSizePanel() {
        initComponents();
    }

    private void initComponents() {
        final JLabel label = new JLabel(Messages.get(Messages.FONT_SIZE) + ": ");
        add(label);
        cbFontSize = new JComboBox<>();
        for (int i = 6; i <= 96; i++) {
            cbFontSize.addItem(i);
        }
        add(cbFontSize);
    }

    public int getValue() {
        return cbFontSize.getItemAt(cbFontSize.getSelectedIndex());
    }

    public void setValue(int value) {
        cbFontSize.setSelectedItem(value);
    }
}
