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
public class EditMenuItems {
    @Getter
    private JMenuItem miUndo;

    @Getter
    private JMenuItem miCut;

    @Getter
    private JMenuItem miCopy;

    @Getter
    private JMenuItem miPaste;

    @Getter
    private JMenuItem miDelete;

    @Getter
    private JMenuItem miFind;

    public EditMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miUndo);
        menu.addSeparator();
        menu.add(miCut);
        menu.add(miCopy);
        menu.add(miPaste);
        menu.add(miDelete);
        menu.addSeparator();
        menu.add(miFind);
    }

    private void initComponents() {
        miUndo = new JMenuItem("Undo");
        miUndo.setMnemonic('U');
        try {
            miUndo.setIcon(ResourceUtils.loadIcon("/icon16x16/undo.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCut = new JMenuItem("Cut");
        miCut.setMnemonic('T');
        try {
            miCut.setIcon(ResourceUtils.loadIcon("/icon16x16/cut.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCopy = new JMenuItem("Copy");
        miCopy.setMnemonic('C');
        try {
            miCopy.setIcon(ResourceUtils.loadIcon("/icon16x16/copy.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miPaste = new JMenuItem("Paste");
        try {
            miPaste.setIcon(ResourceUtils.loadIcon("/icon16x16/paste.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miDelete = new JMenuItem("Delete");
        miDelete.setMnemonic('D');
        try {
            miDelete.setIcon(ResourceUtils.loadIcon("/icon16x16/delete.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miFind = new JMenuItem("Find");
        miFind.setMnemonic('F');
        try {
            miFind.setIcon(ResourceUtils.loadIcon("/icon16x16/find.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
