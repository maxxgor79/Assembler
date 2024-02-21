package ru.retro.assembler.editor.core.ui;

import ru.retro.assembler.editor.core.i18n.Messages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
public class SaveAsChooser extends JFileChooser {
    public SaveAsChooser() {
        initComponent();
    }

    public SaveAsChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initComponent();
    }

    public SaveAsChooser(File currentDirectory) {
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
