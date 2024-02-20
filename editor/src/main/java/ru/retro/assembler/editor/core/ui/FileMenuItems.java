package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
    private JMenuItem miClose;

    @Getter
    private JMenuItem miExit;

    public FileMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miNew);
        menu.addSeparator();
        menu.add(miOpen);
        menu.add(miSave);
        menu.add(miSaveAs);
        menu.add(miSaveAll);
        menu.add(miClose);
        menu.addSeparator();
        menu.add(miExit);
    }

    private void initComponents() {
        miNew = new JMenuItem("New");
        miNew.setMnemonic('N');
        try {
            miNew.setIcon(ResourceUtils.loadIcon("/icon16x16/new.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miOpen = new JMenuItem("Open...");
        miOpen.setMnemonic('O');
        try {
            miOpen.setIcon(ResourceUtils.loadIcon("/icon16x16/open.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSave = new JMenuItem("Save");
        miSave.setMnemonic('S');
        try {
            miSave.setIcon(ResourceUtils.loadIcon("/icon16x16/save.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miSaveAs = new JMenuItem("Save as...");
        miSaveAll = new JMenuItem("Save all");
        miClose = new JMenuItem("Close");
        try {
            miClose.setIcon(ResourceUtils.loadIcon("/icon16x16/close.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miExit = new JMenuItem("Exit");
        miExit.setMnemonic('E');
        try {
            miExit.setIcon(ResourceUtils.loadIcon("/icon16x16/exit.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
