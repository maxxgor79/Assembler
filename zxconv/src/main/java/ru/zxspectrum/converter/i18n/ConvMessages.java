package ru.zxspectrum.converter.i18n;

import java.util.ResourceBundle;

public final class ConvMessages {
    public static final String INPUT_FILE = "input_file";

    public static final String OUTPUT_FILE = "output_file";

    public static final String INPUT_FORMAT = "input_format";

    public static final String OUTPUT_FORMAT = "output_format";

    public static final String FILE_ENUM = "file_enum";

    public static final String WRITTEN_BY = "written_by";

    public static final String DESCRIPTION = "description";

    public static final String INPUT_FILE_REQUIRED = "input_file_required";

    public static final String FILE_NOT_EXIST = "file_not_exist";

    public static final String UNKNOWN_INPUT_FORMAT = "unknown_input_format";

    public static final String UNKNOWN_OUTPUT_FORMAT = "unknown_output_format";

    public static final String UNSUPPORTED_CONVERSION = "unsupported_conversion";

    private ConvMessages() {

    }

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.ConvMessages");

    public static String getMessage(String s) {
        return resourceBundle.getString(s);
    }
}
