package ru.retro.assembler.i8080.editor.core.ui;

import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public class ImportFileDialog extends LocalizedOpenChooser {
    public ImportFileDialog() {
        initComponent();
    }

    public ImportFileDialog(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initComponent();
    }

    public ImportFileDialog(File currentDirectory) {
        super(currentDirectory);
        initComponent();
    }

    private void initComponent() {
        setDialogTitle(I8080Messages.getInstance().get(I8080Messages.IMPORT_FILE));
        setMultiSelectionEnabled(true);
        FileNameExtensionFilter extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages
                .BINARY_FILES), "bin");
        addChoosableFileFilter(extension);
    }
}
