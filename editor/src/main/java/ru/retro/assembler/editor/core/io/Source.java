package ru.retro.assembler.editor.core.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.PureJavaCrc32;
import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import ru.retro.assembler.editor.core.util.UIUtils;

import java.io.*;

/**
 * Author: Maxim Gorin
 * Date: 21.02.2024
 */
@Slf4j
@EqualsAndHashCode(exclude = "textArea")
public class Source {
    private final PureJavaCrc32 crc32Calculator = new PureJavaCrc32();

    @Getter
    private RSyntaxTextArea textArea;

    @Getter
    @NonNull
    private File file;

    @Getter
    private long crc32;

    private Source() {
        this.file = null;
        this.textArea = null;
    }

    public Source(final File file) {
        this(file, null);
    }

    public Source(@NonNull final File file, final String content) {
        this.file = file;
        setContent(content);
    }

    public void rename(@NonNull final File file) {
        this.file = file;
    }

    public String getName() {
        return file == null ? null : file.getName();
    }

    public boolean isNew() {
        return file == null || !file.exists();
    }

    public boolean hasChanges() {
        if (textArea == null) {
            return false;
        }
        final byte[] data = getContent().getBytes();//TODO add charset
        crc32Calculator.reset();
        crc32Calculator.update(data, 0, data.length);
        return crc32 != crc32Calculator.getValue();
    }

    public void load() throws IOException {
        load(file, null);
    }

    public void load(@NonNull final File file, String encoding) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            final byte[] data = IOUtils.toByteArray(fis);
            setContent(encoding == null ? new String(data) : new String(data, encoding));
            this.file = file;
            crc32Calculator.reset();
            crc32Calculator.update(data, 0, data.length);
            this.crc32 = crc32Calculator.getValue();
        }
    }

    public void save() throws IOException {
        save(file);
    }

    public void save(@NonNull String encoding) throws IOException {
        save(file, encoding);
    }

    public void save(@NonNull final File file) throws IOException {
        save(file, null);
    }

    public void save(@NonNull final File file, String encoding) throws IOException {
        if (textArea == null) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            final byte[] data = encoding == null ? getContent().getBytes() : getContent().getBytes(encoding);
            fos.write(data);
            this.file = file;
            crc32Calculator.reset();
            crc32Calculator.update(data, 0, data.length);
            this.crc32 = crc32Calculator.getValue();
        }
    }

    public String getContent() {
        if (textArea == null) {
            return null;
        }
        return textArea.getText();
    }

    public void setContent(final String text) {
        if (textArea == null) {
            textArea = UIUtils.createTextArea(file);
        }
        textArea.setText(text == null ? "" : text);
    }
}
