package ru.retro.assembler.editor.core.access;

import lombok.NonNull;

import java.lang.reflect.Field;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public class Property {
    private Object obj;

    private Field field;

    public Property(@NonNull Object obj, @NonNull Field field) {
        this.obj = obj;
        this.field = field;
    }

    public boolean isInt() {
        return field.getType() == int.class || field.getType() == Integer.class;
    }

    public int getInt() throws IllegalAccessException {
        return field.getInt(obj);
    }

    public boolean isReal() {
        return field.getType() == double.class || field.getType() == Double.class;
    }

    public double getReal() throws IllegalAccessException {
        return field.getDouble(obj);
    }

    public boolean isText() {
        return field.getType() == String.class;
    }

    public String getText() throws IllegalAccessException {
        return field.get(obj).toString();
    }

    public boolean isBoolean() {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    public boolean getBoolean() throws IllegalAccessException {
        return field.getBoolean(obj);
    }

    public void set(int val) throws IllegalAccessException {
        field.setInt(obj, val);
    }

    public void set(double val) throws IllegalAccessException {
        field.setDouble(obj, val);
    }

    public void set(float val) throws IllegalAccessException {
        field.setDouble(obj, val);
    }

    public void set(@NonNull String s) throws IllegalAccessException {
        field.set(obj, s);
    }

    public void set(boolean val) throws IllegalAccessException {
        field.setBoolean(obj, val);
    }

    public String getName() {
        return field.getName();
    }
}
