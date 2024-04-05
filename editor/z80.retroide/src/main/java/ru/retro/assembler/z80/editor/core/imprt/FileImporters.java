package ru.retro.assembler.z80.editor.core.imprt;

import lombok.NonNull;
import ru.retro.assembler.editor.core.control.Controller;
import ru.retro.assembler.editor.core.imprt.FileImporterFactory;

/**
 * @Author: Maxim Gorin
 * Date: 04.04.2024
 */
public final class FileImporters {
    private FileImporters() {

    }

    public static FileImporterFactory defaultFileImporterFactory() {
        return file -> new Z80FileImporter();
    }
}
