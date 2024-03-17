package ru.retro.assembler.editor.core.ui.menu;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.ui.Controller;
import ru.retro.assembler.editor.core.ui.components.MenuItem;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

@Slf4j
public abstract class AbstractMenuItem implements MenuItem {
    protected final String text;

    protected final KeyStroke keyStroke;

    protected final char mnmc;

    protected Icon icon;

    protected final Controller controller;

    protected AbstractMenuItem(@NonNull Controller controller, @NonNull String text, char mnmc, KeyStroke keyStroke
            , String iconPath) {
        this.controller = controller;
        this.text = text;
        this.mnmc = mnmc;
        this.keyStroke = keyStroke;
        if (iconPath != null) {
            try {
                icon = ResourceUtils.loadIcon(iconPath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String name() {
        return text;
    }

    @Override
    public char mnemonic() {
        return mnmc;
    }

    @Override
    public KeyStroke keystroke() {
        return keyStroke;
    }

    @Override
    public Icon icon() {
        return icon;
    }
}
