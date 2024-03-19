package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import lombok.NonNull;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class FontPanel extends JPanel {
    private JLabel label;

    @Getter
    private final JComboBox<String> cbFont = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames());

    public FontPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout());
        label = new JLabel(Messages.getInstance().get(Messages.FONT) + ":");
        add(label);
        add(cbFont);
    }

    public String getSelectedFontName() {
        return cbFont.getItemAt(cbFont.getSelectedIndex());
    }

    public void setSelectedFontName(@NonNull String fontName) {
        cbFont.setSelectedItem(fontName);
    }
}
