package ru.retro.assembler.editor.core.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.PureJavaCrc32;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
@Slf4j
@EqualsAndHashCode
public class Source {
    private final PureJavaCrc32 crc32Calculator = new PureJavaCrc32();
    @Setter
    @Getter
    @NonNull
    private String content;

    @Getter
    @NonNull
    private File file;

    @Getter
    private long crc32;

    public Source() {
        this.file = null;
        this.content = null;
    }

    public Source(@NonNull File file) {
        this.file = file;
        this.content = null;
    }

    public Source(@NonNull File file, @NonNull String content) {
        this.file = file;
        this.content = content;
    }

    public String getName() {
        return file == null ? null : file.getName();
    }

    public boolean isNew() {
        return file == null || !file.exists();
    }

    public boolean hasChanges() {
        if (content == null) {
            return false;
        }
        final byte[] data = content.getBytes();//TODO add charset
        crc32Calculator.reset();
        crc32Calculator.update(data, 0, data.length);
        return crc32 != crc32Calculator.getValue();
    }

    public void load() throws IOException {
        load(file);
    }

    public void load(@NonNull File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            final byte[] data = IOUtils.toByteArray(fis);
            content = new String(data);//TODO add charset
            this.file = file;
            crc32Calculator.reset();
            crc32Calculator.update(data, 0, data.length);
            this.crc32 = crc32Calculator.getValue();
        }
    }

    public void save() throws IOException {
        save(file);
    }

    public void save(@NonNull File file) throws IOException {
        if (content == null) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            final byte[] data = content.getBytes();//TODO add character set
            fos.write(data);
            this.file = file;
            crc32Calculator.reset();
            crc32Calculator.update(data, 0, data.length);
            this.crc32 = crc32Calculator.getValue();
        }
    }
}
