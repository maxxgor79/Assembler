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

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public abstract class Settings extends AnnotationProcessor {
    private final PropertiesConfiguration configs = new PropertiesConfiguration();

    protected void flush() throws IllegalAccessException {
        configs.clear();
        for (Property p : getProperties(this)) {
            if (p.isBoolean()) {
                configs.setProperty(p.getName(), String.valueOf(p.getBoolean()));
            } else if (p.isInt()) {
                configs.setProperty(p.getName(), String.valueOf(p.getInt()));
            } else if (p.isReal()) {
                configs.setProperty(p.getName(), String.valueOf(p.getReal()));
            } else if (p.isText()) {
                configs.setProperty(p.getName(), String.valueOf(p.getText()));
            }
        }
    }

    public void save(@NonNull OutputStream os) throws ConfigurationException {
        try {
            flush();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException(e);
        }
        configs.save(new OutputStreamWriter(os));
    }

    protected void apply() throws IllegalAccessException {
        for (Property p : getProperties(this)) {
            final String name = p.getName();
            if (configs.containsKey(name)) {
                if (p.isBoolean()) {
                    p.set(configs.getBoolean(name));
                } else if (p.isInt()) {
                    p.set(configs.getInt(name));
                } else if (p.isReal()) {
                    p.set(configs.getDouble(name));
                } else if (p.isText()) {
                    p.set(configs.getString(name));
                }
            }
        }
    }

    public void load(@NonNull InputStream is) throws ConfigurationException {
        configs.clear();
        configs.load(new InputStreamReader(is));
        try {
            apply();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException(e);
        }
    }
}