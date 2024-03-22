package ru.retro.assembler.i8080.editor.core.i18n;

import lombok.NonNull;

import java.util.ResourceBundle;

public final class I8080Messages {
    private ResourceBundle bundle;

    private static I8080Messages instance;

    public static I8080Messages getInstance() {
        if (instance == null) {
            instance = new I8080Messages();
        }
        return instance;
    }

    public static final String ABOUT = "about";

    public static final String CLOSE = "close";

    public static final String CAPTION = "caption";

    public static final String BUILD = "build";

    public static final String BUILT_DATE = "built_on";

    public static final String WRITTEN_BY = "written_by";

    public static final String OPEN_FILE = "open_file";

    public static final String ASM_SOURCES = "asm_sources";

    public static final String HEADER_SOURCES = "header_sources";

    public static final String MCSASM_SOURCES = "mcsasm_sources";

    public static final String CANCEL = "cancel";

    public static final String SAVE2 = "save2";

    public static final String FILE_NAME = "file_name";

    public static final String FILES_OF_TYPE = "files_of_type";

    public static final String SAVE_IN = "save_in";

    public static final String LIST = "list";

    public static final String DETAILS = "details";

    public static final String HOME = "home";

    public static final String UP_FOLDER = "up_folder";

    public static final String NEW_FOLDER = "new_folder";

    public static final String SAVE_AS = "save_as";

    public static final String FILE_NOT_FOUND = "file_not_found";

    public static final String ERROR = "error";

    public static final String IO_ERROR = "io_error";

    public static final String EMBEDDED_NOT_FOUND = "embedded_not_found";

    public static final String COMPILE = "compile";

    public static final String COMPILE_RKM = "compile_rkm";

    public static final String COMPILE_WAV = "compile_wav";

    public static final String TAPE_PLAYER = "tape_player";

    private I8080Messages() {
        bundle = ResourceBundle.getBundle("i18n.I8080EditorMessages");
    }

    public String get(@NonNull String s) {
        return bundle.getString(s);
    }
}
