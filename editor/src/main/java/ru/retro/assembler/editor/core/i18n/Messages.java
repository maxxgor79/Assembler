package ru.retro.assembler.editor.core.i18n;

import lombok.NonNull;

import java.util.ResourceBundle;

/**
 * @Author: Maxim Gorin
 * Date: 19.02.2024
 */
public final class Messages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n.Messages");

    private Messages() {

    }

    public static String get(@NonNull String s) {
        return BUNDLE.getString(s);
    }
}
