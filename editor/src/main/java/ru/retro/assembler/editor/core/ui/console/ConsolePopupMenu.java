package ru.retro.assembler.editor.core.ui.console;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
@Slf4j
public class ConsolePopupMenu extends JPopupMenu {
    @Getter
    private JMenuItem miClean;

    @Getter
    private JMenuItem miCopyText;


    public ConsolePopupMenu() {
        initComponents();
        add(miClean);
        addSeparator();
        add(miCopyText);
    }

    private void initComponents() {
        miClean = new JMenuItem(Messages.get(Messages.CLEAN));
        miClean.setMnemonic('L');
        try {
            miClean.setIcon(ResourceUtils.loadIcon("/icon16x16/erase.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCopyText = new JMenuItem(Messages.get(Messages.COPY_TEXT));
        miCopyText.setMnemonic('C');
        try {
            miCopyText.setIcon(ResourceUtils.loadIcon("/icon16x16/copy.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
