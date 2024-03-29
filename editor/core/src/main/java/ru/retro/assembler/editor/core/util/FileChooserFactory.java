package ru.retro.assembler.editor.core.util;

import javax.swing.*;

public interface FileChooserFactory {
    JFileChooser newOpenChooser();

    JFileChooser newSaveChooser();

    JFileChooser newImportChooser();
}
