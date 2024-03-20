package ru.retro.assembler.editor.core.access;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class AnnotationProcessor {
    private void scanProperties(Class<?> clazz, Object o, List<Property> list) {
        for (final Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            f.setAccessible(true);
            if (f.getDeclaredAnnotations() != null) {
                for (Annotation a : f.getDeclaredAnnotations()) {
                    if (a.annotationType().equals(Setting.class)) {
                        list.add(new Property(o, f));
                    }
                }
            }

        }
    }
    protected Collection<Property> getProperties(Object obj) {
        final LinkedList<Property> list = new LinkedList<>();
        Class<?> clazz = obj.getClass();
        do {
            scanProperties(clazz, obj, list);
            clazz = clazz.getSuperclass();
        } while(clazz != null);
        return list;
    }
}
