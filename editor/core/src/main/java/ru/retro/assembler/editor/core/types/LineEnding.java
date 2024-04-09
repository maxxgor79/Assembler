package ru.retro.assembler.editor.core.types;

import lombok.Getter;
import lombok.NonNull;

public enum LineEnding {
    LF("\n"), CR("\r"), CRLF("\r\n"), LFCR("\n\r"), EOL(new String(new byte[]{(byte) 0x9b})),
    ZX80(new String(new byte[]{(byte) 0x76}));

    @Getter
    private String value;

    LineEnding(@NonNull String value) {
        this.value = value;
    }

    public static LineEnding evaluateOf(@NonNull String value) {
        for (LineEnding lineEnding : values()) {
            if (lineEnding.value.equals(value)) {
                return lineEnding;
            }
        }
        return null;
    }

    public static LineEnding getDefault() {
        return evaluateOf(System.lineSeparator());
    }

    public static LineEnding valueOfOrDefault(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
        }
        return getDefault();
    }
}
