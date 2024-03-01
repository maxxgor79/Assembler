package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class EditorAppearancePanel extends JPanel {
    @Getter
    private FontPanel fontPanel;

    @Getter
    private  FontSizePanel fontSizePanel;

    @Getter
    private ColorPanel bkColorPanel;

    public EditorAppearancePanel() {
        initComponents();
    }

    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder(Messages.get(Messages.EDITOR)));
        setLayout(new FlowLayout(FlowLayout.LEADING));
        fontPanel = new FontPanel();
        add(fontPanel);
        fontSizePanel = new FontSizePanel();
        add(fontSizePanel);
        bkColorPanel = new ColorPanel(Messages.get(Messages.BACKGROUND_COLOR) + ":");
        bkColorPanel.setColor(Color.WHITE);
        add(bkColorPanel);
    }
}
