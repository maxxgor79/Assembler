package ru.assembler.zxspectrum.text;

import java.util.ResourceBundle;

public final class Z80Messages {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.Z80Messages");

    public static final String FILE_ENUM = "file_enum";

    public static final String O_STRICT_CONVERSION = "strict_conversion";

    public static final String O_PRODUCE_TAP = "produce_tap";

    public static final String O_PRODUCE_WAV = "produce_wav";

    public static final String O_PRODUCE_TZX = "produce_tzx";

    public static final String O_USE_SPECIAL_PROCESSOR = "use_special_processor";

    public static final String O_PLATFORM_ENCODING = "platform_encoding";

    public static final String O_SOURCE_ENCODING = "source_encoding";

    public static final String O_BYTE_ORDER = "byte_order";

    public static final String O_OUTPUT_DIRECTORY = "output_directory";

    public static final String O_ORG_ADDRESS = "org_address";

    public static final String O_MINIMAL_ADDRESS = "minimal_address";

    public static final String O_MAXIMAL_ADDRESS = "maximal_address";

    public static final String NO_INPUT_FILES = "no_input_files";

    public static final String PROGRAM_WELCOME = "programWelcome";

    public static final String WRITTEN_BY = "writtenBy";

    private Z80Messages() {

    }

    public static String getMessage(String s) {
        return resourceBundle.getString(s);
    }
}
