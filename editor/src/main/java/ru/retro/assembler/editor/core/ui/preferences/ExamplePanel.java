package ru.retro.assembler.editor.core.ui.preferences;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 14.03.2024
 */
public class ExamplePanel extends JPanel {
    @Getter
    private JLabel label;

    public ExamplePanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder());
        label = new JLabel(Messages.get(Messages.EXAMPLE));
        add(label);
    }
}
