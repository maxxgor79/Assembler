package ru.retro.assembler.editor.core.ui.media;

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

    private byte[] buf;

    private int off;

    private int len;

    private final int[] x = new int[1000];

    private final int[] y = new int[1000];

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
        final int width = screenPanel.getWidth();
        final int height = screenPanel.getHeight();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.YELLOW);
        g.drawLine(0, height / 2, width, height / 2);
        if (buf != null && buf.length >= len) {
            double dx = (double) len / width;
            double dy = (double) height / 256.0;
            double offset = off;
            g.setColor(Color.GREEN);
            for (int i = 0; i < width; i++, offset += dx) {
                int b = buf[(int) offset] & 0xff;
                int y = (int) ((255 - b) * dy);
                this.x[i] = i;
                this.y[i] = y;
            }
            g.drawPolyline(x, y, width);
        }
    }

    public void updateData(byte[] buf, int off, int len) {
        this.buf = buf;
        this.off = off;
        this.len = len;
        repaint();
    }

    public void reset() {
        buf = null;
        off = 0;
        len = 0;
        repaint();
    }
}
