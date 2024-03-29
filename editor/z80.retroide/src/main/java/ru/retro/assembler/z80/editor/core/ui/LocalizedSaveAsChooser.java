package ru.retro.assembler.z80.editor.core.ui;

import ru.retro.assembler.editor.core.i18n.Messages;
import ru.retro.assembler.editor.core.util.FileUtils;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class LocalizedSaveAsChooser extends JFileChooser {
    static {
        UIManager.put("FileChooser.cancelButtonText", Z80Messages.getInstance().get(Z80Messages.CANCEL));
        UIManager.put("FileChooser.saveButtonText", Z80Messages.getInstance().get(Z80Messages.SAVE2));
        UIManager.put("FileChooser.fileNameLabelText", Z80Messages.getInstance().get(Z80Messages.FILE_NAME));
        UIManager.put("FileChooser.filesOfTypeLabelText", Z80Messages.getInstance().get(Z80Messages.FILES_OF_TYPE));
        UIManager.put("FileChooser.saveInLabelText", Z80Messages.getInstance().get(Z80Messages.SAVE_IN));
        UIManager.put("FileChooser.listViewButtonToolTipText", Z80Messages.getInstance().get(Z80Messages.LIST));
        UIManager.put("FileChooser.listViewButtonAccessibleName", Z80Messages.getInstance().get(Z80Messages.LIST));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", Z80Messages.getInstance().get(Z80Messages.DETAILS));
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", Z80Messages.getInstance().get(Z80Messages.DETAILS));
        UIManager.put("FileChooser.homeFolderToolTipText", Z80Messages.getInstance().get(Z80Messages.HOME));
        UIManager.put("FileChooser.homeFolderAccessibleName", Z80Messages.getInstance().get(Z80Messages.HOME));
        UIManager.put("FileChooser.upFolderToolTipText", Z80Messages.getInstance().get(Z80Messages.UP_FOLDER));
        UIManager.put("FileChooser.upFolderAccessibleName", Z80Messages.getInstance().get(Z80Messages.UP_FOLDER));
        UIManager.put("FileChooser.newFolderToolTipText", Z80Messages.getInstance().get(Z80Messages.NEW_FOLDER));
        UIManager.put("FileChooser.acceptAllFileFilterText", Z80Messages.getInstance().get(Z80Messages.ALL_FILES));
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
        setDialogTitle(Z80Messages.getInstance().get(Z80Messages.SAVE_AS));
        FileNameExtensionFilter extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages
                .getInstance().ASM_SOURCES), "asm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages.HEADER_SOURCES), "hasm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages.Z80_SOURCES), "z80");
        addChoosableFileFilter(extension);
    }

    @Override
    public File getSelectedFile() {
        if (super.getSelectedFile() == null) {
            return null;
        }
        if (super.getFileFilter() instanceof FileNameExtensionFilter) {
            final FileNameExtensionFilter filter = (FileNameExtensionFilter) super.getFileFilter();
            if (filter.getExtensions() != null && (filter.getExtensions().length > 0)) {
                return FileUtils.addExt(super.getSelectedFile(), filter.getExtensions()[0]);
            }
        }
        return super.getSelectedFile();
    }

    @Override
    public File[] getSelectedFiles() {
        if (super.getSelectedFiles() == null) {
            return null;
        }
        if (super.getFileFilter() instanceof FileNameExtensionFilter) {
            final FileNameExtensionFilter filter = (FileNameExtensionFilter) super.getFileFilter();
            if (filter.getExtensions() != null && (filter.getExtensions().length > 0)) {
                final List<File> fileList = new LinkedList<>();
                for (File file : super.getSelectedFiles()) {
                    fileList.add(FileUtils.addExt(file, filter.getExtensions()[0]));
                }
                return fileList.toArray(new File[fileList.size()]);
            }
        }
        return super.getSelectedFiles();
    }
}
