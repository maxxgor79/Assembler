package ru.retro.assembler.i8080.editor.core.ui;

import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;
import ru.retro.assembler.i8080.editor.core.i18n.I8080Messages;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class OpenFileChooser extends LocalizedOpenChooser {

    public OpenFileChooser() {
        initComponent();
    }

    public OpenFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initComponent();
    }

    public OpenFileChooser(File currentDirectory) {
        super(currentDirectory);
        initComponent();
    }

    private void initComponent() {
        setDialogTitle(I8080Messages.getInstance().get(I8080Messages.OPEN_FILE));
        setMultiSelectionEnabled(true);
        FileNameExtensionFilter extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages
                .ASM_SOURCES), "asm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages.HEADER_SOURCES), "hasm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(I8080Messages.getInstance().get(I8080Messages.MCSASM_SOURCES), "mcs");
        addChoosableFileFilter(extension);
    }
}
