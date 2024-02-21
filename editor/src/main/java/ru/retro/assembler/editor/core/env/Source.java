package ru.retro.assembler.editor.core.env;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
@EqualsAndHashCode
public class Source {
    @Setter
    @Getter
    @NonNull
    private String content;

    @Getter
    @Setter
    @NonNull
    private File file;

    public Source(@NonNull File file, @NonNull String content) {
        this.file = file;
        this.content = content;
    }

    public String getName() {
        return file == null ? null : file.getName();
    }

    public boolean hasChanges() {
        return false;
    }
}
