package ru.retro.assembler.i8080.editor.core.ui;

import ru.retro.assembler.editor.core.util.FileChooserFactory;

import javax.swing.*;

public final class FileChoosers {
    private FileChoosers() {

    }

    public static FileChooserFactory defaultFileChooserFactory() {
        return new FileChooserFactory() {
            @Override
            public JFileChooser newOpenChooser() {
                return new OpenFileChooser();
            }

            @Override
            public JFileChooser newSaveChooser() {
                return new LocalizedSaveAsChooser();
            }
        };
    }
}
