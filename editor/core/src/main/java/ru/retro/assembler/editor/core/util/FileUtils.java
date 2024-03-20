package ru.retro.assembler.editor.core.util;

import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

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

    public static String addExt(@NonNull final String file, @NonNull final String ext) {
        final String fileExt = FilenameUtils.getExtension(file);
        if (fileExt != null && !fileExt.trim().isEmpty()) {
            return file;
        }
        return file + "." + ext;
    }

    public static File addExt(@NonNull final File file, @NonNull final String ext) {
        final String filePath = file.getAbsolutePath();
        final String fileExt = FilenameUtils.getExtension(filePath);
        if (fileExt != null && !fileExt.trim().isEmpty()) {
            return file;
        }
        return new File(filePath + "." + ext);
    }
}
