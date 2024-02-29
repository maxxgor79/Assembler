package ru.retro.assembler.editor.core.i18n;

import lombok.NonNull;

import java.util.ResourceBundle;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public final class Messages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n.Messages");

    private Messages() {

    }

    public static final String OPEN_FILE = "open_file";

    public static final String SAVE_AS = "save_as";

    public static final String ASM_SOURCES = "asm_sources";

    public static final String HEADER_SOURCES = "header_sources";

    public static final String Z80_SOURCES = "z80_sources";

    public static final String FILE = "file";

    public static final String EDIT = "edit";

    public static final String BUILD = "build";

    public static final String TOOLS = "tools";

    public static final String HELP = "help";

    public static final String NEW_FILE = "new_file";

    public static final String SAVE_FILE = "save_file";

    public static final String RELOAD_ALL_FILES = "reload_all_files";

    public static final String ABOUT = "about";

    public static final String NEW = "new";

    public static final String OPEN = "open";

    public static final String SAVE = "save";

    public static final String SAVE_ALL = "save_all";

    public static final String CLOSE = "close";

    public static final String EXIT = "exit";

    public static final String OUTPUT = "output";

    public static final String COMPILE = "compile";

    public static final String UNDO = "undo";

    public static final String CUT = "cut";

    public static final String COPY = "copy";

    public static final String PASTE = "paste";

    public static final String FIND = "find";

    public static final String DELETE = "delete";

    public static final String PREFERENCES = "preferences";

    public static final String CLOSE_ALL = "close_all";

    public static final String CAPTION = "caption";

    public static final String CLEAN = "clean";

    public static final String COPY_TEXT = "copy_text";

    public static final String OPEN2 = "open2";

    public static final String CANCEL = "cancel";

    public static final String FILE_NAME = "file_name";

    public static final String FILES_OF_TYPE = "files_of_type";

    public static final String LOOK_IN = "look_in";

    public static final String LIST = "list";

    public static final String DETAILS = "details";

    public static final String HOME = "home";

    public static final String UP_FOLDER = "up_folder";

    public static final String NEW_FOLDER = "new_folder";

    public static final String SAVE2 = "save2";

    public static final String SAVE_IN = "save_in";

    public static final String BUILT_ON = "built_on";

    public static final String WRITTEN_BY = "written_by";

    public static final String COMPILE_TAP = "compile_tap";

    public static final String COMPILE_TZX = "compile_tzx";

    public static final String COMPILE_WAV = "compile_wav";

    public static final String COMPILER = "compiler";

    public static final String COMPILE_PATH = "compiler_path";

    public static final String OUTPUT_DIRECTORY = "output_directory";

    public static final String CHOOSE_DIRECTORY = "choose_directory";

    public static final String OTHER = "other";

    public static final String ENCODING = "encoding";

    public static final String ERROR = "error";

    public static final String IO_ERROR = "io_error";

    public static final String ENCODING_ERROR = "encoding_error";

    public static final String SOURCE_CONTAINS_CHANGES = "source_contains_changes";

    public static final String SOURCE_IS_UNSAVED = "source_is_unsaved";

    public static final String CLOSE_APPLICATION = "close_application";

    public static final String FILE_NOT_FOUND = "file_not_found";

    public static final String OVERWRITE_FILE = "overwrite_file";

    public static String get(@NonNull String s) {
        return BUNDLE.getString(s);
    }
}
