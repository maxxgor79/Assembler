package ru.retro.assembler.editor.core.imprt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;

/**
 * Author: Maxim Gorin
 * Date: 29.03.2024
 */
public interface FileImporter {
    boolean isAcceptable(File file);

    String importFile(File file) throws IOException;

    String importFile(File file, String encoding) throws IOException, CharacterCodingException;
}
