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
    @Getter
    private static final Environment instance = new Environment();

    @Getter
    @Setter
    @NonNull
    private String encoding = Charset.defaultCharset().name();

    @Getter
    @Setter
    @NonNull
    private AppSettings settings;

    private Environment() {

    }

    public File getCurrentDirectory() {
        return null;
    }
}
