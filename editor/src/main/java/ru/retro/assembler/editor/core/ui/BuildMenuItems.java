package ru.retro.assembler.editor.core.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.ui.components.AbstractMenu;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
@Slf4j
public class BuildMenuItems extends AbstractMenu {

    public BuildMenuItems(@NonNull JMenu menu) {
        super(menu);
        initComponents();
    }

    private void initComponents() {
    }
}
