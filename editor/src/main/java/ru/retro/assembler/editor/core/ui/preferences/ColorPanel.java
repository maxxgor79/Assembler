package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import lombok.NonNull;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
public class ColorPanel extends JPanel {
    private String caption;

    @Getter
    private JPanel colorPanel;

    public ColorPanel() {
        this("Color:");
    }

    public ColorPanel(String caption) {
        this.caption = caption;
        initComponents();
        initListeners();
    }

    private void initComponents() {
        add(new JLabel(caption));
        colorPanel = new JPanel();
        colorPanel.setSize(new Dimension(48, 48));
        colorPanel.setMinimumSize(new Dimension(48, 48));
        colorPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(colorPanel);
    }

    private void initListeners() {
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final Color c = JColorChooser.showDialog(null, Messages.get(Messages.COLOR_CHOICE), getColor());
                if (c != null) {
                    setColor(c);
                }
            }
        });
    }

    public void setColor(@NonNull Color c) {
        colorPanel.setBackground(c);
    }

    public Color getColor() {
        return colorPanel.getBackground();
    }
}
