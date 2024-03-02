package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 02.03.2024
 */
public class LanguagePanel extends JPanel {
    private static final String [] LANGUAGES = {
        Messages.get(Messages.ENGLISH),
        Messages.get(Messages.RUSSIAN)
    };

    @Getter
    private JComboBox<String> cbLanguages = new JComboBox<>();

    public LanguagePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        final JLabel label1 = new JLabel(Messages.get(Messages.LANGUAGE) + ":");
        add(label1);
        cbLanguages = new JComboBox<>(LANGUAGES);
        add(cbLanguages);
    }
}
