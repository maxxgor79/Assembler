package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2024
 */
public class MiscellaneousPanel extends JPanel {
    @Getter
    private CharsetPanel charsetPanel;

    @Getter
    private LanguagePanel languagePanel;


    public MiscellaneousPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(2, 1));
        add(charsetPanel = new CharsetPanel());
        add(languagePanel = new LanguagePanel());
    }
}
