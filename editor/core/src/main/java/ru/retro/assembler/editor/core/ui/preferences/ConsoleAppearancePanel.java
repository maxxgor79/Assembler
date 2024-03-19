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

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class ConsoleAppearancePanel extends JPanel {
    @Getter
    private FontPanel fontPanel;

    @Getter
    private FontSizePanel fontSizePanel;

    @Getter
    private ColorPanel fontColorPanel;

    @Getter
    private ColorPanel bkColorPanel;

    private ExamplePanel examplePanel;

    public ConsoleAppearancePanel() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        examplePanel = new ExamplePanel();
        setBorder(BorderFactory.createTitledBorder(Messages.getInstance().get(Messages.CONSOLE)));
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
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
        c.anchor = GridBagConstraints.WEST;
        add(fontSizePanel, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(examplePanel, c);

        bkColorPanel = new ColorPanel(Messages.getInstance().get(Messages.BACKGROUND_COLOR) + ":") {
            @Override
            public void setColor(@NonNull Color c) {
                super.setColor(c);
                examplePanel.setBackground(c);
            }
        };
        bkColorPanel.setColor(Color.WHITE);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        add(bkColorPanel, c);

        fontColorPanel = new ColorPanel(Messages.getInstance().get(Messages.FONT_COLOR) + ":") {
            @Override
            public void setColor(@NonNull Color c) {
                super.setColor(c);
                examplePanel.getLabel().setForeground(c);
            }
        };

        fontColorPanel.setColor(Color.BLACK);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        add(fontColorPanel, c);
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
