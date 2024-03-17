package ru.retro.assembler.editor.core.util;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;

public final class FileUtils {
    private FileUtils() {

    }

    public static File createTempFile(@NonNull final File src) throws IOException {
        final File tempFile = new File(src.getParentFile(), src.getName() + ".bak");
        tempFile.createNewFile();
        tempFile.deleteOnExit();
        return tempFile;
    }
}
