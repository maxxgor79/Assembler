package ru.retro.assembler.editor.core.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.PureJavaCrc32;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.UnixLineEndingInputStream;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import ru.retro.assembler.editor.core.types.LineEnding;
import ru.retro.assembler.editor.core.util.FileUtils;
import ru.retro.assembler.editor.core.util.TextUtils;
import ru.retro.assembler.editor.core.util.UIUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Author: Maxim Gorin Date: 21.02.2024
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
        load(file, StandardCharsets.UTF_8.name());
    }

    public void load(@NonNull final File file, String encoding) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (encoding == null) {
            encoding = StandardCharsets.UTF_8.name();
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), encoding)) {
            load(file, reader);
        }
    }

    public void load(@NonNull File file, @NonNull final Reader reader) throws IOException {
        final List<String> lines = IOUtils.readLines(reader);
        final StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s).append(LineEnding.LF.getValue());
        }
        final String text = sb.toString();
        setContent(text);
        final byte[] data = text.getBytes();
        this.file = file;
        crc32Calculator.reset();
        crc32Calculator.update(data, 0, data.length);
        this.crc32 = crc32Calculator.getValue();
    }

    public void save(@NonNull String encoding, @NonNull LineEnding lineEnding) throws IOException {
        save(file, encoding, lineEnding);
    }

    public void save(@NonNull final File file, @NonNull String encoding, @NonNull LineEnding lineEnding)
            throws IOException {
        if (textArea == null) {
            return;
        }
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            final String text = TextUtils.replaceUnixLineEnding(getContent(), lineEnding);
            final byte[] data = text.getBytes(encoding);
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
