package ru.retro.assembler.z80.editor.core.ui;

import ru.retro.assembler.editor.core.ui.LocalizedOpenChooser;
import ru.retro.assembler.z80.editor.core.i18n.Z80Messages;

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
        setDialogTitle(Z80Messages.getInstance().get(Z80Messages.OPEN_FILE));
        setMultiSelectionEnabled(true);
        FileNameExtensionFilter extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages
                .ASM_SOURCES), "asm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages.HEADER_SOURCES), "hasm");
        addChoosableFileFilter(extension);
        extension = new FileNameExtensionFilter(Z80Messages.getInstance().get(Z80Messages.Z80_SOURCES), "z80");
        addChoosableFileFilter(extension);
    }
}
