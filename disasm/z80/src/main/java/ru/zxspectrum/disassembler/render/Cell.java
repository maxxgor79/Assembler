package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author maxim
 * Date: 12/24/2023
 */
public abstract class Cell implements Render {
    @Getter
    @Setter
    private static boolean uppercase = true;

    protected static final String SPACE = " ";

    public static String toUpperOrLowerCase(@NonNull String s) {
        return uppercase ? s.toUpperCase() : s.toLowerCase();
    }
}
