package ru.retro.assembler.i8080.editor.utils;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.SystemUtils;

/**
 * @Author: Maxim Gorin Date: 28.02.2024
 */
public final class CLIUtils {
    public static final String ARG_RKM = "-rkm";

    public static final String ARG_WAV = "-wav";

    public static final String ARG_OUTPUT = "-o";

    public static final String ARG_LOCALE = "-Duser.language=%s";

    public static final String ASM_FILENAME;

    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            ASM_FILENAME = "mcsasm.exe";
        } else {
            ASM_FILENAME = "mcsasm";
        }
    }

    private CLIUtils() {

    }

    public static List<String> toList(@NonNull final String asmFile, @NonNull String lang, @NonNull final String argDir
            , @NonNull final String dir, @NonNull final String source, @NonNull final String... extArgs) {
        final List<String> list = new ArrayList<>();
        list.add(asmFile);
        list.add(lang);
        list.add(argDir);
        list.add(dir);
        if (extArgs != null) {
            for (String arg : extArgs) {
                list.add(arg);
            }
        }
        list.add(source);
        return Collections.unmodifiableList(list);
    }

    public static List<String> toList(@NonNull final String argDir
            , @NonNull final String dir, @NonNull final String source, @NonNull final String... extArgs) {
        final List<String> list = new ArrayList<>();
        list.add(argDir);
        list.add(dir);
        if (extArgs != null) {
            for (String arg : extArgs) {
                list.add(arg);
            }
        }
        list.add(source);
        return Collections.unmodifiableList(list);
    }
}
