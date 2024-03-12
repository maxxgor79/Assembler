package ru.retro.assembler.editor.core.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import lombok.Getter;
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
    @Getter
    private JMenuItem miCompile;

    @Getter
    private JMenuItem miCompileTap;

    @Getter
    private JMenuItem miCompileWav;

    @Getter
    private JMenuItem miCompileTzx;


    public BuildMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miCompile);
        menu.addSeparator();
        menu.add(miCompileTap);
        menu.add(miCompileTzx);
        menu.add(miCompileWav);
    }

    private void initComponents() {
        miCompile = new JMenuItem(Messages.get(Messages.COMPILE));
        miCompile.setMnemonic('C');
        miCompile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_DOWN_MASK));
        try {
            miCompile.setIcon(ResourceUtils.loadIcon("/icon16x16/equipment.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCompileTap = new JMenuItem(Messages.get(Messages.COMPILE_TAP));
        miCompileTzx = new JMenuItem(Messages.get(Messages.COMPILE_TZX));
        miCompileWav = new JMenuItem(Messages.get(Messages.COMPILE_WAV));
    }
}
