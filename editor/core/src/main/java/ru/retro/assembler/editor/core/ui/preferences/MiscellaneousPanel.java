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

    @Getter
    private LineEndingPanel lineEndingPanel;


    public MiscellaneousPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        add(charsetPanel = new CharsetPanel());
        add(languagePanel = new LanguagePanel());
        add(lineEndingPanel = new LineEndingPanel());
    }
}
