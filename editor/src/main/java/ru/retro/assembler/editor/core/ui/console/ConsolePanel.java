package ru.retro.assembler.editor.core.ui.console;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

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

    @Getter
    private ConsolePopup consolePopup;
    public ConsolePanel() {
        initComponents();
    }

    private void initComponents() {
        final Border border = BorderFactory.createTitledBorder(Messages.get(Messages.OUTPUT) + ":");
        setBorder(border);
        setLayout(new BorderLayout());
        final JScrollPane pane = new JScrollPane(area = createTextArea());
        add(pane, BorderLayout.CENTER);
        consolePopup = new ConsolePopup();
        area.setComponentPopupMenu(consolePopup);
    }

    private JTextArea createTextArea() {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        return area;
    }
}
