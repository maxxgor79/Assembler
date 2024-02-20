package ru.retro.assembler.editor.core.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
public class ConsolePanel extends JPanel {
    @Getter
    private JTextArea area;
    public ConsolePanel() {
        initComponents();
    }

    private void initComponents() {
        final Border border = BorderFactory.createTitledBorder("Output:");
        setBorder(border);
        setLayout(new BorderLayout());
        final JScrollPane pane = new JScrollPane(area = createTextArea());
        add(pane, BorderLayout.CENTER);
    }

    private JTextArea createTextArea() {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        return area;
    }
}
