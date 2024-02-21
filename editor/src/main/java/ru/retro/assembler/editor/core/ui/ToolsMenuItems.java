package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
@Slf4j
public class ToolsMenuItems {
    @Getter
    private JMenuItem miPreferences;

    public ToolsMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miPreferences);
    }

    private void initComponents() {
        miPreferences = new JMenuItem(Messages.get(Messages.PREFERENCES));
        try {
            miPreferences.setIcon(ResourceUtils.loadIcon("/icon16x16/preferences.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miPreferences.setMnemonic('P');
    }
}
