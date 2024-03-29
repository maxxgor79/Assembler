package ru.retro.assembler.z80.editor.core.ui;

import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public class ImportFileChooser extends LocalizedOpenChooser {
    public ImportFileChooser() {
        initComponent();
    }

    public ImportFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initComponent();
    }

    public ImportFileChooser(File currentDirectory) {
        super(currentDirectory);
        initComponent();
    }

    private void initComponent() {
        setDialogTitle(Z80Messages.getInstance().get(Z80Messages.IMPORT_FILE));
        setMultiSelectionEnabled(true);
        final FileNameExtensionFilter extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages
                .BINARY_FILES), "bin");
        addChoosableFileFilter(extension);
    }
}
