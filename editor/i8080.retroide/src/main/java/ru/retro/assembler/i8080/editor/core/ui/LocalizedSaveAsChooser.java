package ru.retro.assembler.i8080.editor.core.ui;

import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class LocalizedSaveAsChooser extends JFileChooser {
    static {
        UIManager.put("FileChooser.cancelButtonText", I8080Messages.getInstance().get(I8080Messages.CANCEL));
        UIManager.put("FileChooser.saveButtonText", I8080Messages.getInstance().get(I8080Messages.SAVE2));
        UIManager.put("FileChooser.fileNameLabelText", I8080Messages.getInstance().get(I8080Messages.FILE_NAME));
        UIManager.put("FileChooser.filesOfTypeLabelText", I8080Messages.getInstance().get(I8080Messages.FILES_OF_TYPE));
        UIManager.put("FileChooser.saveInLabelText", I8080Messages.getInstance().get(I8080Messages.SAVE_IN));
        UIManager.put("FileChooser.listViewButtonToolTipText", I8080Messages.getInstance().get(I8080Messages.LIST));
        UIManager.put("FileChooser.listViewButtonAccessibleName", I8080Messages.getInstance().get(I8080Messages.LIST));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", I8080Messages.getInstance().get(I8080Messages.DETAILS));
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", I8080Messages.getInstance().get(I8080Messages.DETAILS));
        UIManager.put("FileChooser.homeFolderToolTipText", I8080Messages.getInstance().get(I8080Messages.HOME));
        UIManager.put("FileChooser.homeFolderAccessibleName", I8080Messages.getInstance().get(I8080Messages.HOME));
        UIManager.put("FileChooser.upFolderToolTipText", I8080Messages.getInstance().get(I8080Messages.UP_FOLDER));
        UIManager.put("FileChooser.upFolderAccessibleName", I8080Messages.getInstance().get(I8080Messages.UP_FOLDER));
        UIManager.put("FileChooser.newFolderToolTipText", I8080Messages.getInstance().get(I8080Messages.NEW_FOLDER));
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
        setDialogTitle(I8080Messages.getInstance().get(I8080Messages.SAVE_AS));
        FileNameExtensionFilter extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages
                .getInstance().ASM_SOURCES), "asm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages.HEADER_SOURCES)
                , "hasm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages.MCSASM_SOURCES)
                , "mcsasm");
        addChoosableFileFilter(extension);
    }
}
