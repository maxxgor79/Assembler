package ru.retro.assembler.editor.core.imprt;

import java.io.File;

/**
 * Author: Maxim Gorin
 * Date: 29.03.2024
 */
public interface FileImporterFactory {
    FileImporter newFileImporter(File file);
}
