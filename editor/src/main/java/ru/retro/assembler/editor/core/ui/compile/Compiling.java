package ru.retro.assembler.editor.core.ui.compile;

import lombok.NonNull;
import ru.retro.assembler.editor.core.io.Source;

public interface Compiling {
    void compile(@NonNull final Source src, String... args);
}
