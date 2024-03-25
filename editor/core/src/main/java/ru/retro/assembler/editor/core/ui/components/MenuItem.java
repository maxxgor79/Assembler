package ru.retro.assembler.editor.core.ui.components;

import javax.swing.*;
import java.awt.event.ActionEvent;

public interface MenuItem extends Order {
    String name();

    char mnemonic();

    KeyStroke keystroke();

    Icon icon();

    boolean hasSeparator();

    void onAction(ActionEvent e);

    boolean isEnabled();
}
