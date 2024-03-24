package ru.retro.assembler.editor.core.settings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import ru.retro.assembler.editor.core.access.AnnotationProcessor;
import ru.retro.assembler.editor.core.access.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
@Slf4j
public abstract class Settings extends AnnotationProcessor {
    private final Preferences prefs = Preferences.userRoot();

    private String getNameWithPrefix(String name) {
        if (getPrefix() != null) {
            name = getPrefix() + "." + name;
        }
        return name;
    }

    protected void flush() throws IllegalAccessException {
        for (Property p : getProperties(this)) {
            final String prefixedName = getNameWithPrefix(p.getName());
            if (p.isBoolean()) {
                prefs.putBoolean(prefixedName, p.getBoolean());
            } else if (p.isInt()) {
                prefs.putInt(prefixedName, p.getInt());
            } else if (p.isReal()) {
                prefs.putDouble(prefixedName, p.getReal());
            } else if (p.isText()) {
                prefs.put(prefixedName, String.valueOf(p.getText()));
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
            final String prefixedName = getNameWithPrefix(p.getName());
            final String textValue = prefs.get(prefixedName, null);
            if (textValue != null) {
                if (p.isBoolean()) {
                    p.set(prefs.getBoolean(prefixedName, false));
                } else if (p.isInt()) {
                    p.set(prefs.getInt(prefixedName, 0));
                } else if (p.isReal()) {
                    p.set(prefs.getDouble(prefixedName, 0.0));
                } else if (p.isText()) {
                    p.set(evaluateVariables(textValue));
                }
            } else {
                if (p.isText()) {
                    p.set(evaluateVariables(p.getText()));
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

    private String evaluateVariables(String s) {
        if (s == null) {
            return s;
        }
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            sb.setLength(0);
            sb.append("${").append(entry.getKey()).append("}");
            s = StringUtils.replace(s, sb.toString(), entry.getValue().toString());
        }
        return s;
    }

    public abstract String getPrefix();
}
