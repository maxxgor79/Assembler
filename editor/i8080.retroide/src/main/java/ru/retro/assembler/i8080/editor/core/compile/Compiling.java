package ru.retro.assembler.i8080.editor.core.compile;

import lombok.NonNull;
import ru.retro.assembler.editor.core.io.Source;
import ru.retro.assembler.editor.core.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface Compiling {
    void compile(@NonNull final Source src, String... args);

    default String toArgument(Source src, @NonNull String encoding) throws IOException {
        if (!src.isNew() && !src.hasChanges()) {
            return src.getFile().getAbsolutePath();
        }
        final Path tmpPath = FileUtils.createTempFile(src.getFile()).toPath();
        Files.write(tmpPath, src.getTextArea().getText().getBytes(encoding), StandardOpenOption.TRUNCATE_EXISTING);
        final StringBuilder sb = new StringBuilder(tmpPath.toFile().getAbsolutePath());
        if (src.isNew()) {
            sb.append("#").append(src.getFile().getName());
        } else {
            sb.append("#").append(src.getFile().getAbsolutePath());
        }
        return sb.toString();
    }
}
