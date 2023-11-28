package ru.zxspectrum.assembler.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2023
 */
@Slf4j
public final class FileUtil {
    private FileUtil() {
    }

    public static File createNewFileSameName(@NonNull File dir, @NonNull File source, String ext) {
        String fileName = source.getName();
        int index = fileName.indexOf('.');
        if (index != -1) {
            fileName = fileName.substring(0, index);
        }
        if (ext != null && !ext.trim().isEmpty()) {
            fileName = fileName + "." + ext;
        }
        return new File(dir, fileName);
    }

    public static void safeClose(@NonNull Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

}
