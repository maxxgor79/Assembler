package ru.retro.assembler.editor.core.settings;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import ru.retro.assembler.editor.core.access.AnnotationProcessor;
import ru.retro.assembler.editor.core.access.Property;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public abstract class Settings extends AnnotationProcessor {
    private final Preferences prefs = Preferences.userRoot();

    protected void flush() throws IllegalAccessException {
        for (Property p : getProperties(this)) {
            if (p.isBoolean()) {
                prefs.putBoolean(p.getName(), p.getBoolean());
            } else if (p.isInt()) {
                prefs.putInt(p.getName(), p.getInt());
            } else if (p.isReal()) {
                prefs.putDouble(p.getName(), p.getReal());
            } else if (p.isText()) {
                prefs.put(p.getName(), String.valueOf(p.getText()));
            }
        }
    }

    public void save() throws BackingStoreException {
        try {
            flush();
            prefs.flush();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new BackingStoreException(e);
        }
    }

    protected void apply() throws IllegalAccessException {
        for (Property p : getProperties(this)) {
            final String name = p.getName();
            if (prefs.get(name, null) != null) {
                if (p.isBoolean()) {
                    p.set(prefs.getBoolean(name, false));
                } else if (p.isInt()) {
                    p.set(prefs.getInt(name, 0));
                } else if (p.isReal()) {
                    p.set(prefs.getDouble(name, 0.0));
                } else if (p.isText()) {
                    p.set(prefs.get(name, null));
                }
            }
        }
    }

    public void load() throws BackingStoreException {
        try {
            apply();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new BackingStoreException(e);
        }
    }
}
