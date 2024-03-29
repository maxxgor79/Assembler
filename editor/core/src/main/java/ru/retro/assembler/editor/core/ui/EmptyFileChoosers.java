package ru.retro.assembler.editor.core.ui;

import ru.retro.assembler.editor.core.util.FileChooserFactory;

import javax.swing.*;

public final class EmptyFileChoosers {
    private EmptyFileChoosers() {

    }

    public static FileChooserFactory defaultFileChooserFactory() {
        return new FileChooserFactory() {
            @Override
            public JFileChooser newOpenChooser() {
                return null;
            }

            @Override
            public JFileChooser newSaveChooser() {
                return null;
            }

            @Override
            public JFileChooser newImportChooser() {
                return null;
            }
        };
    }
}
