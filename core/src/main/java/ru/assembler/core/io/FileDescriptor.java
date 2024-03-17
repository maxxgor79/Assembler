package ru.assembler.core.io;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;


public class FileDescriptor {
    @Getter
    @NonNull
    private File file;

    @Getter
    @Setter
    @NonNull
    private String display;

    public FileDescriptor(final File file) {
        setFile(file);
    }

    public FileDescriptor(@NonNull final String display) {
        this.display = display;
    }

    public FileDescriptor(final File file, @NonNull final String display) {
        this.display = display;
        setFile(file);
    }

    public void setFile(@NonNull final File file) {
        this.file = file;
        if (display == null) {
            display = this.file.getAbsolutePath();
        }
    }
}
