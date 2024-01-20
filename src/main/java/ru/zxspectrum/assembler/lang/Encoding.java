package ru.zxspectrum.assembler.lang;

import lombok.Getter;

/**
 * @author Maxim Gorin
 */
public enum Encoding {
    ASCII("ASCII"),

    KOI8_R("KOI8-R"),

    Windows1250("Windows-1250"),

    Windows1251("Windows-1251"),

    Windows1252("Windows-1252"),

    Windows1253("Windows-1253"),

    CP1251("CP1251"),

    MacRoman("MacRoman"),

    MacCyrillic("MacCyrillic"),

    UTF_8("UTF-8"),

    UTF_16("UTF-16");

    @Getter
    private final String name;

    Encoding(String name) {
        this.name = name;
    }

    public static Encoding valueByName(String name) {
        for (Encoding encoding : values()) {
            if (encoding.name.equals(name)) {
                return encoding;
            }
        }
        return UTF_8;
    }

    public static int sizeof(Encoding encoding) {
        return switch (encoding) {
            case MacRoman, MacCyrillic, ASCII, Windows1250, Windows1251
                    , Windows1252, Windows1253, CP1251, KOI8_R -> 1;
            case UTF_8, UTF_16 -> 2;
        };
    }
}
