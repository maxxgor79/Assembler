package ru.retro.assembler.editor.core.imprt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.Collection;

/**
 * Author: Maxim Gorin
 * Date: 29.03.2024
 */
public interface FileImporter {
    boolean isAcceptable(File file);

    Collection<SourceDescriptor> importFile(File file) throws IOException;

    Collection<SourceDescriptor> importFile(File file, String encoding) throws IOException, CharacterCodingException;
}
