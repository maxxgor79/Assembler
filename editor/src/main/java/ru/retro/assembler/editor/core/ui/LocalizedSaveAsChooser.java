package ru.retro.assembler.editor.core.ui;

import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class LocalizedSaveAsChooser extends JFileChooser {
    static {
        UIManager.put("FileChooser.cancelButtonText", Messages.get(Messages.CANCEL));
        UIManager.put("FileChooser.saveButtonText", Messages.get(Messages.SAVE2));
        UIManager.put("FileChooser.fileNameLabelText", Messages.get(Messages.FILE_NAME));
        UIManager.put("FileChooser.filesOfTypeLabelText", Messages.get(Messages.FILES_OF_TYPE));
        UIManager.put("FileChooser.saveInLabelText", Messages.get(Messages.SAVE_IN));
        UIManager.put("FileChooser.listViewButtonToolTipText", Messages.get(Messages.LIST));
        UIManager.put("FileChooser.listViewButtonAccessibleName", Messages.get(Messages.LIST));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", Messages.get(Messages.DETAILS));
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", Messages.get(Messages.DETAILS));
        UIManager.put("FileChooser.homeFolderToolTipText", Messages.get(Messages.HOME));
        UIManager.put("FileChooser.homeFolderAccessibleName", Messages.get(Messages.HOME));
        UIManager.put("FileChooser.upFolderToolTipText", Messages.get(Messages.UP_FOLDER));
        UIManager.put("FileChooser.upFolderAccessibleName", Messages.get(Messages.UP_FOLDER));
        UIManager.put("FileChooser.newFolderToolTipText", Messages.get(Messages.NEW_FOLDER));
    }
    public LocalizedSaveAsChooser() {
        initComponent();
    }

    public LocalizedSaveAsChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initComponent();
    }

    public LocalizedSaveAsChooser(File currentDirectory) {
        super(currentDirectory);
        initComponent();
    }

    private void initComponent() {
        setDialogTitle(Messages.get(Messages.SAVE_AS));
        FileNameExtensionFilter extension = new FileNameExtensionFilter(Messages.get(Messages.ASM_SOURCES), "asm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Messages.get(Messages.HEADER_SOURCES), "hasm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Messages.get(Messages.Z80_SOURCES), "z80");
        addChoosableFileFilter(extension);
    }
}
