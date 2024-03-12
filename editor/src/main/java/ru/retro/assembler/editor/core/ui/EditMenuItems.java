package ru.retro.assembler.editor.core.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.ResourceUtils;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
    private JMenuItem miSelectAll;

    @Getter
    private JMenuItem miFind;

    @Getter
    private JMenuItem miFindNext;

    public EditMenuItems(@NonNull JMenu menu) {
        initComponents();
        menu.add(miUndo);
        menu.addSeparator();
        menu.add(miCut);
        menu.add(miCopy);
        menu.add(miPaste);
        menu.add(miDelete);
        menu.addSeparator();
        menu.add(miSelectAll);
        menu.addSeparator();
        menu.add(miFind);
        menu.add(miFindNext);
    }

    private void initComponents() {
        miUndo = new JMenuItem(Messages.get(Messages.UNDO));
        miUndo.setMnemonic('U');
        miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        try {
            miUndo.setIcon(ResourceUtils.loadIcon("/icon16x16/undo.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCut = new JMenuItem(Messages.get(Messages.CUT));
        miCut.setMnemonic('T');
        miCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        try {
            miCut.setIcon(ResourceUtils.loadIcon("/icon16x16/cut.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miCopy = new JMenuItem(Messages.get(Messages.COPY));
        miCopy.setMnemonic('C');
        miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        try {
            miCopy.setIcon(ResourceUtils.loadIcon("/icon16x16/copy.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miPaste = new JMenuItem(Messages.get(Messages.PASTE));
        miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        try {
            miPaste.setIcon(ResourceUtils.loadIcon("/icon16x16/paste.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miDelete = new JMenuItem(Messages.get(Messages.DELETE));
        miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        miDelete.setMnemonic('D');

        miSelectAll = new JMenuItem(Messages.get(Messages.SELECT_ALL));
        miSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        miSelectAll.setMnemonic('A');

        miFind = new JMenuItem(Messages.get(Messages.FIND));
        miFind.setMnemonic('F');
        miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        try {
            miFind.setIcon(ResourceUtils.loadIcon("/icon16x16/find.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        miFindNext = new JMenuItem(Messages.get(Messages.FIND_NEXT));
        miFindNext.setMnemonic('E');
        miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
    }
}
