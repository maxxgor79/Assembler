package ru.retro.assembler.editor.core.access;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class AnnotationProcessor {
    protected Collection<Property> getProperties(Object obj) {
        final LinkedList<Property> list = new LinkedList<>();
        for (final Field f : obj.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            f.setAccessible(true);
            if (f.getDeclaredAnnotations() != null) {
                for (Annotation a : f.getDeclaredAnnotations()) {
                    if (a.annotationType().equals(Setting.class)) {
                        list.add(new Property(obj, f));
                    }
                }
            }

        }
        return list;
    }
}
