package ru.retro.assembler.editor.core.imprt;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

/**
 * Author: Maxim Gorin
 * Date: 29.03.2024
 */
public final class FileImporters {
    private FileImporters() {

    }
    public static FileImporterFactory defaultFileImporterFactory() {
        return file -> new FileImporter() {
            @Override
            public boolean isAcceptable(File file) {
                return false;
            }

            @Override
            public String importFile(@NonNull File file) throws IOException {
                return importFile(file, StandardCharsets.UTF_8.name());
            }

            @Override
            public String importFile(@NonNull File file, @NonNull String encoding) throws IOException
                    , CharacterCodingException {
                return null;
            }
        };
    }
}
