package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import ru.retro.assembler.editor.core.ui.components.AbstractMenu;

/**
 * @Author: Maxim Gorin
 * Date: 21.03.2024
 */
@Slf4j
public class RunMenuItems extends AbstractMenu {
    public RunMenuItems(@NonNull JMenu menu) {
      super(menu);
      initComponents();
    }

    private void initComponents() {
    }
}
