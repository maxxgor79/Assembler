package ru.retro.assembler.editor.core.ui;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 20.02.2024
 */
@Slf4j
public class BuildMenuItems {
    private JMenuItem miCompile;

    public BuildMenuItems(@NonNull JMenuItem menu) {
        initComponents();
        menu.add(miCompile);
    }

    private void initComponents() {
        miCompile = new JMenuItem(Messages.get(Messages.COMPILE));
        miCompile.setMnemonic('C');
        try {
            miCompile.setIcon(ResourceUtils.loadIcon("/icon16x16/equipment.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
