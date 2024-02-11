package ru.zxspectrum.converter.core;

import lombok.NonNull;

public enum Format {
    Raw("raw"), Wav("wav"), Tap("tap"), Tzx("tzx");

    Format(@NonNull String code) {
        this.code = code;
    }

    private final String code;

    public static Format getByCode(String code) {
        for (Format f : values()) {
            if (f.code.equalsIgnoreCase(code)) {
                return f;
            }
        }
        return null;
    }
}
