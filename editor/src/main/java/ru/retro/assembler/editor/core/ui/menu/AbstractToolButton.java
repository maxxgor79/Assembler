package ru.retro.assembler.editor.core.ui.menu;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.components.ToolButton;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@Slf4j
public abstract class AbstractToolButton implements ToolButton {
    private final String text;

    private final String hint;

    private final Dimension size;

    private Icon icon;

    private final Controller controller;

    public AbstractToolButton(@NonNull Controller controller, String text, int width, int height, String hint
            , String iconPath) {
        this.controller = controller;
        this.text = text;
        this.size = new Dimension(width, height);
        this.hint = hint;
        if (iconPath != null) {
            try {
                icon = ResourceUtils.loadIcon(iconPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public AbstractToolButton(@NonNull Controller controller, String text, String hint
            , String iconPath) {
        this(controller, text, ICON_WIDTH, ICON_HEIGHT, hint, iconPath);
    }

    @Override
    public String name() {
        return text;
    }

    @Override
    public Dimension size() {
        return size;
    }

    @Override
    public String hint() {
        return hint;
    }

    @Override
    public Icon icon() {
        return icon;
    }
}
