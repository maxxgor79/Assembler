package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import lombok.NonNull;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

    private ExamplePanel examplePanel;

    public EditorAppearancePanel() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        examplePanel = new ExamplePanel();
        setBorder(BorderFactory.createTitledBorder(Messages.get(Messages.EDITOR)));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        fontPanel = new FontPanel();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 2, 0, 2);
        add(fontPanel, c);

        fontSizePanel = new FontSizePanel();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        add(fontSizePanel, c);

        bkColorPanel = new ColorPanel(Messages.get(Messages.BACKGROUND_COLOR) + ":") {
            @Override
            public void setColor(@NonNull Color c) {
                super.setColor(c);
                examplePanel.setBackground(c);
            }
        };
        bkColorPanel.setColor(Color.WHITE);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        add(bkColorPanel, c);

        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(examplePanel, c);
    }

    private void initListeners() {
        fontPanel.getCbFont().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                final String fontName = (String) e.getItem();
                Font font = UIUtils.createFont(fontName, fontSizePanel.getValue());
                examplePanel.getLabel().setFont(font);
            }
        });

        fontSizePanel.getCbFontSize().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Integer value = (Integer) e.getItem();
                Font font = UIUtils.createFont(fontPanel.getCbFont().getSelectedItem().toString(), value);
                examplePanel.getLabel().setFont(font);
            }
        });
    }
}
