package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
@Slf4j
public class SourcePopupMenu extends JPopupMenu {
    @Getter
    private JMenuItem miClose;

    @Getter
    private JMenuItem miSave;

    @Getter
    private JMenuItem miSaveAs;

    public SourcePopupMenu() {
        initComponents();
        add(miSave);
        add(miSaveAs);
        addSeparator();
        add(miClose);
    }

    private void initComponents() {
        miClose = new JMenuItem(Messages.getInstance().get(Messages.CLOSE));
        try {
            miClose.setIcon(ResourceUtils.loadIcon("/icon16x16/close.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSave = new JMenuItem(Messages.getInstance().get(Messages.SAVE));
        miSave.setMnemonic('S');
        try {
            miSave.setIcon(ResourceUtils.loadIcon("/icon16x16/save.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSaveAs = new JMenuItem(Messages.getInstance().get(Messages.SAVE_AS));
    }

    @Override
    public void setEnabled(boolean enabled) {
        miClose.setEnabled(enabled);
        miSave.setEnabled(enabled);
        miSaveAs.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
