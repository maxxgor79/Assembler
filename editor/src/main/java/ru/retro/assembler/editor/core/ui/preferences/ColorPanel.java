package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2024
 */
@Slf4j
public class ColorPanel extends JPanel {
    private String caption;

    private JPanel colorPanel;

    private java.util.List<ActionListener> listenerList = new LinkedList<>();

    public ColorPanel() {
        this(Messages.get(Messages.COLOR) + ":");
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
                final ActionEvent event = new ActionEvent(ColorPanel.this, e.getID(), "mouse", e.getWhen()
                        , 0);
                process(event);
            }
        });
    }

    public void setColor(@NonNull Color c) {
        colorPanel.setBackground(c);
    }

    public Color getColor() {
        return colorPanel.getBackground();
    }

    public void addActionListener(@NonNull ActionListener l) {
        listenerList.add(l);
    }

    public void removeActionListener(@NonNull ActionListener l) {
        listenerList.remove(l);
    }

    protected void process(ActionEvent e) {
        for (ActionListener l : listenerList) {
            try {
                l.actionPerformed(e);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
