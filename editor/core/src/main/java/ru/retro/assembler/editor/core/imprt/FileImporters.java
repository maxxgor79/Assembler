package ru.retro.assembler.editor.core.imprt;

/**
 * @Author: Maxim Gorin
 * Date: 29.03.2024
 */
public final class FileImporters {
    private FileImporters() {

    }

    public static FileImportFactory defaultFileImporter() {
        return file -> null;
    }
}
