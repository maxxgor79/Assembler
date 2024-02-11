package ru.zxspectrum.converter.text;

import lombok.NonNull;

public final class Formatter {
    private Formatter() {

    }

    public static String format(@NonNull String s, String ... args) {
        return String.format(s, args);
    }
}
