package ru.retro.assembler.editor.core.ui.player;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 21.03.2024
 */
public class InteractivePanel extends JPanel {

    private JPanel screenPanel;

    @Getter
    private JSlider slider;

    public InteractivePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        screenPanel = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                paintScreen(g);
            }
        };
        slider = new JSlider(0, 999, 0);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 4, 1, 4);
        add(screenPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        add(slider, c);
    }

    protected void paintScreen(Graphics g) {
        g.setColor(Color.BLACK);
        Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        g.setColor(Color.YELLOW);
        g.drawLine(clip.x, clip.y + clip.height / 2, clip.x + clip.width, clip.y + clip.height / 2);
    }

    public void setData(byte []data) {

    }
}
