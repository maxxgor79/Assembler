package ru.retro.assembler.editor.core.ui;

import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2024
 */
public class DefaultOpenChooser extends JFileChooser {
    static {
        UIManager.put("FileChooser.cancelButtonText", Messages.get(Messages.CANCEL));
        UIManager.put("FileChooser.openButtonText", Messages.get(Messages.OPEN2));
        UIManager.put("FileChooser.fileNameLabelText", Messages.get(Messages.FILE_NAME));
        UIManager.put("FileChooser.filesOfTypeLabelText", Messages.get(Messages.FILES_OF_TYPE));
        UIManager.put("FileChooser.lookInLabelText", Messages.get(Messages.LOOK_IN));
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

    public DefaultOpenChooser() {
    }

    public DefaultOpenChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public DefaultOpenChooser(File currentDirectory) {
        super(currentDirectory);
    }
}
