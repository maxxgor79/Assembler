package ru.retro.assembler.editor.core.ui.address;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

/**
 * @Author: Maxim Gorin
 * Date: 05.04.2024
 */
@Slf4j
public class AddressPanel extends JPanel {
    private JLabel label;

    @Getter
    private JFormattedTextField tfAddress;

    public AddressPanel() {
        initComponents();
    }

    private void initComponents() {
        label = new JLabel(Messages.getInstance().get(Messages.ENTER_ADDRESS));
        try {
            tfAddress = new JFormattedTextField(new MaskFormatter("#####"));
        } catch (ParseException e) {
            log.info(e.getMessage(), e);
        }
        tfAddress.setColumns(5);
        add(label);
        add(tfAddress);
    }
}
