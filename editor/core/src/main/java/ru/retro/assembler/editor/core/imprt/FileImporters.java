package ru.retro.assembler.editor.core.imprt;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;

/**
 * Author: Maxim Gorin
 * Date: 29.03.2024
 */
public final class FileImporters {
    private FileImporters() {

    }
    public static FileImportFactory defaultFileImporter() {
        return file -> new FileImporter() {
            @Override
            public boolean isAcceptable(File file) {
                return false;
            }

            @Override
            public String importFile(@NonNull File file) throws IOException {
                return "empty text";
            }

            @Override
            public String importFile(@NonNull File file, @NonNull String encoding) throws IOException
                    , CharacterCodingException {
                return "empty text";
            }
        };
    }
}
