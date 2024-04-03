package ru.zxspectrum.disassembler.utils;

import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public final class FileUtils {
    private FileUtils() {

    }

    public static File createNewFileSameName(@NonNull File dir, @NonNull File source, String ext) {
        if (ext != null && ext.trim().isEmpty()) {
            throw new IllegalArgumentException("Bad extension: " + ext);
        }
        String fileName = FilenameUtils.removeExtension(source.getName());
        if (ext != null) {
            fileName = fileName + "." + ext;
        }
        return new File(dir, fileName);
    }

}
