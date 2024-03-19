package ru.retro.assembler.editor.core.ui.find;

import lombok.Getter;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Maxim Gorin
 * Date: 29.02.2024
 */
public class TextFieldPanel extends JPanel {
    @Getter
    private JTextField tfText;

    public TextFieldPanel() {
        initComponents();
    }
    private void initComponents() {
        tfText = new JTextField(32);
        tfText.setToolTipText(Messages.getInstance().get(Messages.ENTER_TEXT_TO_FIND));
        add(tfText);
    }
}
