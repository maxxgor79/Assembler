package ru.assembler.core.error.text;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

/**
 * @author Maxim Gorin
 */
@Slf4j
public final class Messages {
    public static final String INVALID_NUMBER_FORMAT = "invalidNumberFormat";

    public static final String INVALID_HEXADECIMAL_NUMBER_FORMAT = "invalidHexadecimalNumberFormat";

    public static final String INVALID_BINARY_NUMBER_FORMAT = "invalidBinaryNumberFormat";

    public static final String INVALID_OCTAL_NUMBER_FORMAT = "invalidOctalNumberFormat";

    public static final String DIVIDING_BY_ZERO = "dividingByZero";

    public static final String IDENTIFIER_EXPECTED = "identifierExpected";

    public static final String EXPECTED_SYMBOL = "expectedSymbol";

    public static final String BAD_CHARSET_ENCODING = "badCharsetEncoding";

    public static final String CHAR_TOO_LONG = "charTooLong";

    public static final String UNEXPECTED_SYMBOL = "unexpectedSymbol";

    public static final String UNKNOWN_COMMAND = "unknownCommand";

    public static final String DUPLICATED_COMMAND = "duplicatedCommand";

    public static final String INVALID_TABLE_FORMAT = "invalidLineFormat'";

    public static final String IS_EXPECTED = "isExpected";

    public static final String EXPRESSION = "expression";

    public static final String LABEL_IS_ALREADY_DEFINED = "labelIsAlreadyDefined";

    public static final String INTERNAL_ERROR = "internalError";

    public static final String ADDRESS_OUT_OF_RANGE = "addressOutOfRange";

    public static final String VALUE_OUT_OF_RANGE = "valueOutOfRange";

    public static final String ADDRESS_EXCEPTED = "addressExcepted";

    public static final String VALUE_EXCEPTED = "valueExcepted";

    public static final String FILE_PATH_EXCEPTED = "filePathExcepted";

    public static final String NOT_IMPLEMENTED_YET = "notImplementedYet";

    public static final String FILE_NOT_FOUND = "fileNotFound";

    public static final String FILE_READ_ERROR = "fileReadError";

    public static final String CYCLIC_DEPENDENCIES_ERROR = "cyclicDependenciesError";

    public static final String COMPILED1 = "compiled1";

    public static final String SOURCES = "sources";

    public static final String LINES = "lines";

    public static final String LABEL_NOT_FOUND = "LabelIsNotFound";

    public static final String ERROR = "error";

    public static final String WARNING = "warning";

    public static final String SUCCESSFULLY = "successfully";

    public static final String COMPILING = "compiling";

    public static final String N_WARNINGS = "n_warnings";

    public static final String VARIABLE_NOT_FOUND = "variableNotFound";

    public static final String IDENTIFIER_EXPECTED_FOUND = "identifierExpectedFound";

    public static final String VARIABLE_IS_ALREADY_DEFINED = "variableIsAlreadyDefined";

    public static final String UNEXPECTED_LABEL = "unexpectedLabel";

    public static final String VARIABLE_PATTERNS_ARE_NOT_EQUAL = "variablePatternsAreNotEqual";

    public static final String COMMAND_DATA_IS_NOT_LOADED = "commandDataIsNotLoaded";

    public static final String CONSTANT_VALUE_REQUIRED = "constantValueRequired";

    public static final String UNKNOWN_IDENTIFIER = "unknownIdentifier";

    public static final String LOSS_PRECISION_TYPE_FOR = "lossPrecisionTypeFor";

    public static final String IDENTIFIER = "identifier";

    public static final String NUMBER = "number";

    public static final String DELIMITER = "delimiter";

    public static final String STRING = "string";

    public static final String COMMENT = "comment";

    public static final String VARIABLE = "variable";

    public static final String CHAR = "char";

    public static final String LABEL = "label";

    public static final String EOL = "eol";

    public static final String LABEL_DECLARATION_REQUIRED = "label_declaration_required";

    public static final String FILE_IS_ALREADY_INCLUDED = "file_is_already_included";

    public static final String IMAGE_NOT_SUPPORTED = "image_not_supported";

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("asm.i18n.Messages");

    private Messages() {
    }

    public static String getMessage(@NonNull String s) {
        return resourceBundle.getString(s);
    }
}
