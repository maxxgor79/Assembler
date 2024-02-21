package ru.retro.assembler.editor.core.env;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.retro.assembler.editor.core.settings.AppSettings;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @Author: Maxim Gorin
 * Date: 21.02.2024
 */
@Slf4j
public class Environment {
    private static final String NONAME = "noname";

    @Getter
    private static final Environment instance = new Environment();

    private final Set<Source> sources = new HashSet<>();

    private int nameIndex = 1;

    @Getter
    @Setter
    @NonNull
    private String encoding = Charset.defaultCharset().name();

    @Getter
    @Setter
    @NonNull
    private AppSettings settings;

    protected final List<ActionListener> newListeners = new LinkedList<>();

    protected final List<ActionListener> openListeners = new LinkedList<>();

    protected final List<ActionListener> saveListeners = new LinkedList<>();

    protected final List<ActionListener> saveAsListeners = new LinkedList<>();

    protected final List<ActionListener> closeListeners = new LinkedList<>();

    private Environment() {

    }

    public Source newSource() {
        final File file = new File(createNewFileName());
        final Source source = new Source(file, "");
        add(source);
        processNewListeners(source);
        return source;
    }

    public void addNewListener(@NonNull ActionListener l) {
        newListeners.add(l);
    }

    protected void processNewListeners(@NonNull Source source) {
        for (ActionListener l : newListeners) {
            l.onAction(source);
        }
    }

    public Source openSource(@NonNull String path) throws IOException {
        final File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        if (!file.isFile()) {
            throw new IOException(String.format("%s is not file", path));
        }
        Source source;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = IOUtils.toByteArray(fis);
            source = new Source(file, new String(data));
        }
        add(source);
        processOpenListeners(source);
        return source;
    }

    public void addOpenListener(@NonNull ActionListener l) {
        openListeners.add(l);
    }

    protected void processOpenListeners(@NonNull Source source) {
        for (ActionListener l : openListeners) {
            l.onAction(source);
        }
    }

    public void saveSource(@NonNull Source source) throws IOException {
        final byte[] data = source.getContent().getBytes(encoding);
        try (FileOutputStream fos = new FileOutputStream(source.getFile())) {
            fos.write(data);
        }
        processSaveListeners(source);
    }

    public void addSaveListener(@NonNull ActionListener l) {
        saveListeners.add(l);
    }

    protected void processSaveListeners(@NonNull Source source) {
        for (ActionListener l : saveListeners) {
            l.onAction(source);
        }
    }

    public void saveAll() throws IOException {
        for (Source source : sources) {
            try {
                saveSource(source);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new IOException(source.getFile().getAbsolutePath());
            }
        }
    }

    public void saveSource(@NonNull String path, @NonNull Source source) throws IOException {
        final byte[] data = source.getContent().getBytes(encoding);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
        processSaveAsListeners(source);
    }

    public void addSaveAsListener(@NonNull ActionListener l) {
        saveAsListeners.add(l);
    }

    protected void processSaveAsListeners(@NonNull Source source) {
        for (ActionListener l : saveAsListeners) {
            l.onAction(source);
        }
    }

    public void closeSource(@NonNull Source source) {
        if (remove(source)) {
            processCloseListeners(source);
        }
    }

    public void addCloseListener(@NonNull ActionListener l) {
        closeListeners.add(l);
    }

    protected void processCloseListeners(@NonNull Source source) {
        for (ActionListener l : closeListeners) {
            l.onAction(source);
        }
    }

    public void closeSources() {
        for (Source source : sources) {
            closeSource(source);
        }
    }

    public void reloadAll() throws IOException {
        for (Source source : sources) {
            try {
                openSource(source.getFile().getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new IOException(source.getFile().getAbsolutePath());
            }
        }
    }

    public File getCurrentDirectory() {
        return null;
    }

    protected String createNewFileName() {
        return String.format("%s%d", NONAME, nameIndex++);
    }

    protected boolean add(@NonNull Source source) {
        if (sources.contains(source)) {
            return false;
        }
        return sources.add(source);
    }

    protected boolean remove(@NonNull Source source) {
        if (!sources.contains(source)) {
            return false;
        }
        return sources.remove(source);
    }

    public interface ActionListener {
        void onAction(Source source);
    }
}
