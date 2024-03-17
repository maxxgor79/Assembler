package ru.retro.assembler.editor.core.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public interface ToolButton {
    int ICON_WIDTH = 32;

    int ICON_HEIGHT = 32;

    int order();

    String name();

    Dimension size();

    String hint();

    Icon icon();

    boolean hasSeparator();

    boolean isEnabled();

    void onAction(ActionEvent e);
}
