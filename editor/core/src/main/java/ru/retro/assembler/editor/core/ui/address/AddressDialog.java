package ru.retro.assembler.editor.core.ui.address;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.ModalDialog;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 05.04.2024
 */
@Slf4j
public class AddressDialog extends JDialog implements ModalDialog {

    private ButtonPanel btnPanel;

    @Getter
    private AddressPanel addressPanel;

    private int result = CANCEL;

    public AddressDialog() {
        initComponents();
        initListeners();
    }

    public AddressDialog(@NonNull Frame owner) {
        super(owner);
        initComponents();
        initListeners();
    }

    public AddressDialog(@NonNull Window owner) {
        super(owner);
        initComponents();
        initListeners();
    }

    private void initComponents() {
        setTitle(Messages.getInstance().get(Messages.ADDRESS));
        try {
            setIconImage(ResourceUtils.loadImage("/icon16x16/chip.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        setModal(true);
        setLayout(new BorderLayout());
        addressPanel = new AddressPanel();
        add(addressPanel, BorderLayout.NORTH);
        btnPanel = new ButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
        setResizable(false);
        pack();
        SwingUtilities.invokeLater(() -> {
            btnPanel.getBtnOk().requestFocus();
            getRootPane().setDefaultButton(btnPanel.getBtnOk());
        });
    }

    private void initListeners() {
        btnPanel.getBtnOk().addActionListener(e -> {
            result = OK;
            dispose();
        });
    }

    @Override
    public int showModal() {
        setVisible(true);
        return result;
    }

    public BigInteger getAddress() {
        return new BigInteger(addressPanel.getTfAddress().getValue().toString());
    }
}
