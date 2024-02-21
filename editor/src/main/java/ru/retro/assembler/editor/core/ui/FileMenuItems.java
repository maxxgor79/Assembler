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
public class FileMenuItems {
    @Getter
    private JMenuItem miNew;

    @Getter
    private JMenuItem miOpen;

    @Getter
    private JMenuItem miSave;

    @Getter
    private JMenuItem miSaveAs;

    @Getter
    private JMenuItem miSaveAll;

    @Getter
    private JMenuItem miReloadAllFromDisk;

    @Getter
    private JMenuItem miClose;

    @Getter
    private JMenuItem miCloseAll;

    @Getter
    private JMenuItem miExit;

    public FileMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miNew);
        menu.add(miOpen);
        menu.addSeparator();;
        menu.add(miSave);
        menu.add(miSaveAs);
        menu.add(miSaveAll);
        menu.add(miReloadAllFromDisk);
        menu.addSeparator();
        menu.add(miClose);
        menu.add(miCloseAll);
        menu.addSeparator();
        menu.add(miExit);
    }

    private void initComponents() {
        miNew = new JMenuItem(Messages.get(Messages.NEW));
        miNew.setMnemonic('N');
        try {
            miNew.setIcon(ResourceUtils.loadIcon("/icon16x16/new.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miOpen = new JMenuItem(Messages.get(Messages.OPEN));
        miOpen.setMnemonic('O');
        try {
            miOpen.setIcon(ResourceUtils.loadIcon("/icon16x16/open.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSave = new JMenuItem(Messages.get(Messages.SAVE));
        miSave.setMnemonic('S');
        try {
            miSave.setIcon(ResourceUtils.loadIcon("/icon16x16/save.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSaveAs = new JMenuItem(Messages.get(Messages.SAVE_AS));
        miSaveAll = new JMenuItem(Messages.get(Messages.SAVE_ALL));
        miReloadAllFromDisk = new JMenuItem(Messages.get(Messages.RELOAD_ALL_FILES));
        try {
            miReloadAllFromDisk.setIcon(ResourceUtils.loadIcon("/icon16x16/refresh.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miClose = new JMenuItem(Messages.get(Messages.CLOSE));
        try {
            miClose.setIcon(ResourceUtils.loadIcon("/icon16x16/close.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCloseAll = new JMenuItem(Messages.get(Messages.CLOSE_ALL));
        miExit = new JMenuItem(Messages.get(Messages.EXIT));
        miExit.setMnemonic('E');
        try {
            miExit.setIcon(ResourceUtils.loadIcon("/icon16x16/exit.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
